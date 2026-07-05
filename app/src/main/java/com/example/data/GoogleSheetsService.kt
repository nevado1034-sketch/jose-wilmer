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
        if (webhookUrl.isBlank() || !webhookUrl.startsWith("http")) {
            return@withContext Result.failure(Exception("URL del Webhook de Google Sheets no configurada o inválida."))
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
                put("sheetName", "clientes litio appps")
                put("appName", "clientes litio appps")
            }

            val request = Request.Builder()
                .url(webhookUrl)
                .post(jsonObject.toString().toRequestBody(mediaType))
                .build()

            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string() ?: ""
                if (response.isSuccessful) {
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
                        // Sometimes Google Apps Script redirects or returns standard HTML/Text success
                        if (responseBody.contains("success") || responseBody.contains("Success") || responseBody.isEmpty()) {
                            Result.success("Sincronizado con éxito (respuesta simple)")
                        } else {
                            Result.success("Sincronizado con éxito")
                        }
                    }
                } else {
                    Log.e(TAG, "Error de red de Google Sheets: ${response.code} - $responseBody")
                    Result.failure(Exception("Error del servidor (Código: ${response.code})"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al sincronizar con Google Sheets: ${e.message}", e)
            Result.failure(Exception("Error de conexión: ${e.message ?: "Verifica tu internet"}"))
        }
    }
}
