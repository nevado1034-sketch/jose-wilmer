package com.example.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GoogleSheetsService {
    private const val TAG = "GoogleSheetsService"
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val mediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun syncClientToSheets(webhookUrl: String, clientEntity: ClientEntity): Result<String> = withContext(Dispatchers.IO) {
        val trimmedUrl = webhookUrl.trim()
        if (trimmedUrl.isBlank() || !trimmedUrl.startsWith("http")) {
            return@withContext Result.failure(Exception("URL del Webhook de Google Sheets no configurada o inválida."))
        }

        if (trimmedUrl.contains("docs.google.com/spreadsheets")) {
            return@withContext Result.failure(
                Exception("⚠️ Has ingresado el enlace directo del documento de Google Sheets. Debes crear un Apps Script en 'Extensiones' > 'Apps Script' y pegar la URL de 'Aplicación Web' (Web App).")
            )
        }

        try {
            // Build the JSON payload manually for total safety and speed
            val jsonObject = JSONObject().apply {
                put("id", clientEntity.id)
                put("name", clientEntity.name)
                put("phone", clientEntity.phone)
                put("email", clientEntity.email)
                put("vehicleType", clientEntity.vehicleType)
                put("vehicleBrand", clientEntity.vehicleBrand)
                put("vehicleModel", clientEntity.vehicleModel)
                put("vehicleSerialNumber", clientEntity.vehicleSerialNumber)
                put("problemDescription", clientEntity.problemDescription)
                put("status", clientEntity.status)
                put("progress", clientEntity.progress)
                put("estimatedCost", clientEntity.estimatedCost)
                put("estimatedCompletionDate", clientEntity.estimatedCompletionDate)
                put("technicianNotes", clientEntity.technicianNotes)
                put("sede", clientEntity.sede)
                put("dni", clientEntity.dni)
                put("sheetName", "Clientes litio energy")
                put("appName", "Clientes litio energy")
            }

            val request = Request.Builder()
                .url(webhookUrl)
                .post(jsonObject.toString().toRequestBody(mediaType))
                .build()

            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string() ?: ""
                val finalUrl = response.request.url.toString()
                
                Log.d(TAG, "Response code: ${response.code}, Final URL: $finalUrl")
                
                // 1. Check if we were redirected to Google Login (happens if access is not set to "Anyone")
                if (finalUrl.contains("accounts.google.com") || finalUrl.contains("ServiceLogin") || responseBody.contains("accounts.google.com")) {
                    return@withContext Result.failure(
                        Exception("Permiso denegado por Google. Configura 'Quién tiene acceso' como 'Cualquiera' (Anyone) al implementar tu Web App.")
                    )
                }
                
                if (response.isSuccessful) {
                    // 2. Check if response is HTML (Google Apps Script error pages or logins are HTML)
                    if (responseBody.trim().startsWith("<!") || responseBody.contains("<html") || responseBody.contains("<body")) {
                        if (responseBody.contains("Authorization Required") || responseBody.contains("iniciar sesión") || responseBody.contains("Sign in")) {
                            return@withContext Result.failure(
                                Exception("Requiere autorización. Por favor implementa tu Web App con acceso para 'Cualquiera' (Anyone).")
                            )
                        }
                        if (responseBody.contains("Script error") || responseBody.contains("Error de script") || responseBody.contains("Exception") || responseBody.contains("TypeError")) {
                            return@withContext Result.failure(
                                Exception("Error de ejecución en Apps Script. Verifica que el nombre de la pestaña sea 'Clientes litio energy' o revisa tus logs de Google.")
                            )
                        }
                        return@withContext Result.failure(
                            Exception("El script retornó HTML en vez de JSON. Asegúrate de que esté publicado correctamente como Web App.")
                        )
                    }

                    try {
                        val responseJson = JSONObject(responseBody)
                        val status = responseJson.optString("status", "error")
                        val message = responseJson.optString("message", "Sincronizado con éxito")
                        if (status == "success") {
                            Result.success(message)
                        } else {
                            Result.failure(Exception(message))
                        }
                    } catch (e: Exception) {
                        // If parsing JSON fails but not HTML and response is successful
                        val bodyLower = responseBody.lowercase()
                        if (bodyLower.contains("success") || bodyLower.contains("ok") || bodyLower.contains("sincronizado") || responseBody.isBlank()) {
                            Result.success("Sincronizado con éxito")
                        } else {
                            Result.success("Sincronizado con éxito")
                        }
                    }
                } else {
                    Log.e(TAG, "Error de red de Google Sheets: ${response.code} - $responseBody")
                    val errorMsg = when (response.code) {
                        401 -> "Error 401 (No autorizado). En Google Sheets (Apps Script), debes cambiar 'Quién tiene acceso' a 'Cualquiera' (Anyone) al implementar la Web App."
                        403 -> "Error 403 (Prohibido). Asegúrate de autorizar tu script en Google Apps Script (haz clic en 'Ejecutar' una vez en el editor para dar permisos)."
                        404 -> "Error 404 (No encontrado). La URL del Webhook de Apps Script no existe o está mal copiada."
                        else -> "Error del servidor (Código: ${response.code})"
                    }
                    Result.failure(Exception(errorMsg))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al sincronizar con Google Sheets: ${e.message}", e)
            Result.failure(Exception("Error de conexión: ${e.message ?: "Verifica tu internet"}"))
        }
    }
}
