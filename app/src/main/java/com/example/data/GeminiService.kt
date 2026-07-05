package com.example.data

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val TAG = "GeminiService"
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val mediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun getChatResponse(prompt: String, chatHistory: List<Pair<String, Boolean>>): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        
        // Check if API key is valid / has been updated from the placeholder
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY" || apiKey == "GEMINI_API_KEY") {
            Log.d(TAG, "API Key is missing or placeholder. Running fallback bot.")
            return@withContext getLocalFallbackResponse(prompt)
        }

        try {
            // Build conversational history array
            val historyBuilder = StringBuilder()
            chatHistory.forEach { (text, isUser) ->
                val role = if (isUser) "user" else "model"
                val safeText = text.replace("\\", "\\\\")
                                  .replace("\"", "\\\"")
                                  .replace("\n", "\\n")
                                  .replace("\r", "\\r")
                historyBuilder.append("""{"role": "$role", "parts": [{"text": "$safeText"}]},""")
            }

            val systemInstructionText = """
                Eres LitioBot, el asistente inteligente de 'LITIO ENERGY SERVICIO TECNICO' (Especialistas en scooters, bicicletas y motos eléctricas).
                Tu propósito principal es educar a los usuarios sobre preguntas, errores, fallas, códigos de error y mantenimiento de sus vehículos eléctricos (EV).
                Brindas ayuda rápida, amigable y muy profesional a los clientes de nuestro taller.
                Tu tono es experto, educativo y servicial. Ofreces consejos técnicos claros sobre:
                - Códigos de error comunes de scooters (por ejemplo, en Xiaomi: Error 14/15 en acelerador/freno, Error 18 en sensores Hall del motor, Error 21 en comunicación con el BMS de la batería).
                - Fallas de batería (pérdida de autonomía, celdas desbalanceadas, BMS bloqueado, sobrecalentamiento).
                - Problemas mecánicos (frenos hidráulicos, chirridos, pastillas gastadas, neumáticos pinchados y presión óptima de 45-50 PSI).
                - Prevención de daños por agua o cortocircuitos tras lluvias.
                Siempre habla en español.
                Si te preguntan sobre el estado de su reparación, recuérdales que pueden ver el progreso ingresando su número de teléfono en la sección 'CONSULTAR AVANCE'.
                Anímalos a usar el botón "Enviar WhatsApp" para contactar directamente a servicio al cliente con sus datos.
                Mantén tus respuestas relativamente cortas, fáciles de entender y bien organizadas (máximo 3 párrafos o listas cortas).
            """.trimIndent()
            
            val safeInstruction = systemInstructionText.replace("\\", "\\\\")
                                                      .replace("\"", "\\\"")
                                                      .replace("\n", "\\n")
                                                      .replace("\r", "\\r")

            val safePrompt = prompt.replace("\\", "\\\\")
                                   .replace("\"", "\\\"")
                                   .replace("\n", "\\n")
                                   .replace("\r", "\\r")

            val jsonBody = """
            {
              "contents": [
                ${historyBuilder.toString()}
                {"role": "user", "parts": [{"text": "$safePrompt"}]}
              ],
              "systemInstruction": {
                "parts": [{"text": "$safeInstruction"}]
              }
            }
            """.trimIndent()

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(jsonBody.toRequestBody(mediaType))
                .build()

            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    val jsonObject = JSONObject(responseBody)
                    val candidates = jsonObject.getJSONArray("candidates")
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.getJSONObject("content")
                    val parts = content.getJSONArray("parts")
                    val firstPart = parts.getJSONObject(0)
                    firstPart.getString("text")
                } else {
                    Log.e(TAG, "API Error response: $responseBody")
                    getLocalFallbackResponse(prompt)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Network exception: ${e.message}", e)
            getLocalFallbackResponse(prompt)
        }
    }

    private fun getLocalFallbackResponse(prompt: String): String {
        val query = prompt.lowercase()
        return when {
            query.contains("hola") || query.contains("buen") || query.contains("saludos") || query.contains("saludo") -> {
                "¡Hola! Bienvenido a **LITIO ENERGY SERVICIO TECNICO**. ⚡\n\nSoy **LitioBot**, tu asistente experto. Hoy te enseñaré sobre mantenimiento preventivo y solución de fallas. ¿De qué error o falla de tu scooter, bicicleta o moto eléctrica te gustaría aprender hoy?"
            }
            query.contains("error") || query.contains("codigo") || query.contains("código") || query.contains("fallo") || query.contains("falla") || query.contains("led") || query.contains("pitido") -> {
                "⚠️ **Guía de Errores y Fallas Comunes (Litio Energy):**\n\n" +
                "1. **Error 14 / 15 (Xiaomi / Segway):** Falla en el sensor del acelerador o manillar de freno. Se soluciona calibrando el resorte/imán interno o reemplazando el gatillo.\n" +
                "2. **Error 18:** Problema en los sensores Hall del motor brushless. El motor vibra, suena feo o no gira libremente.\n" +
                "3. **Error 21 / 22 / 24:** Fallo de comunicación o voltaje incorrecto con el sistema BMS de la batería de litio.\n" +
                "4. **Fallo de encendido:** Fusible térmico de la controladora quemado o BMS bloqueado por baja tensión extrema (batería muerta).\n\n" +
                "¿Tu equipo muestra un código o luz parpadeante? Cuéntame y te daré el diagnóstico inicial."
            }
            query.contains("agua") || query.contains("lluvia") || query.contains("mojado") || query.contains("humedad") || query.contains("charco") || query.contains("mojar") -> {
                "🌧️ **¡Alerta por Humedad o Lluvia!**\n\n" +
                "Si tu vehículo eléctrico pasó por charcos, lluvia fuerte o se mojó y presenta fallas:\n" +
                "1. **NO lo enciendas** ni intentes ponerlo a cargar. Eso podría fundir la placa controladora o el BMS permanentemente.\n" +
                "2. Retira la tapa si es posible y desconecta la batería principal.\n" +
                "3. Tráelo rápido a **Litio Energy** para un secado térmico profundo y baño químico ultrasónico de placas antes de que se sulfaten las celdas."
            }
            query.contains("motor") || query.contains("rueda") || query.contains("vibra") || query.contains("traba") || query.contains("sonido") -> {
                "⚙️ **Diagnóstico de Fallas en Motores Eléctricos (Hub Motor):**\n\n" +
                "- **Motor frenado o rueda dura (incluso apagado):** Cortocircuito en los transistores MOSFET de tu controladora. ¡No lo fuerces!\n" +
                "- **Motor que tiembla al acelerar:** Cable de fase de corriente (amarillo, azul o verde) sulfatado, desoldado o derretido.\n" +
                "- **Sonido metálico o crujido:** Rodajes (baleros) desgastados u oxidados por entrada de agua.\n\n" +
                "¡Realizamos rebobinado, cambio de sensores Hall y mantenimiento de rodajes!"
            }
            query.contains("bateria") || query.contains("batería") || query.contains("carga") || query.contains("litio") -> {
                "🔋 **Consejo de Litio Energy sobre Baterías:**\n" +
                "1. Evita descargar tu batería a menos del 15% para prolongar sus celdas de litio.\n" +
                "2. No la cargues inmediatamente después de usar tu vehículo; deja que repose 15 minutos para que se enfríe.\n" +
                "3. Utiliza siempre cargadores originales con el voltaje exacto."
            }
            query.contains("freno") || query.contains("pastilla") || query.contains("disco") || query.contains("frenar") -> {
                "🛑 **Mantenimiento de Frenos:**\n" +
                "Si escuchas chirridos al frenar en tu scooter o moto, puede deberse a pastillas desgastadas o discos contaminados con polvo. En Litio Energy realizamos limpieza, calibración y cambio de pastillas cerámicas para una respuesta de frenado óptima."
            }
            query.contains("pinche") || query.contains("llanta") || query.contains("neumatico") || query.contains("neumático") || query.contains("aire") -> {
                "🚲 **Presión de Neumáticos:**\n" +
                "En scooters eléctricos (como Xiaomi o Dualtron), mantener la presión de aire entre 45 y 50 PSI es fundamental para evitar pellizcos de cámara (pinchazos internos) y maximizar la autonomía de la batería. ¿Deseas cambiar a llantas sólidas antipinchazos?"
            }
            query.contains("precio") || query.contains("costo") || query.contains("cuanto") || query.contains("cuánto") || query.contains("tarifa") -> {
                "💵 **Tarifas y Costos Estimados (S/.):**\n" +
                "- Diagnóstico Inicial: S/. 20 (¡Gratis si realizas la reparación con nosotros!)\n" +
                "- Cambio de Cámara/Neumático: S/. 30 - 50\n" +
                "- Mantenimiento Preventivo General: S/. 80 - 120\n" +
                "- Reparación de Motor / Controladora: Previa evaluación en taller."
            }
            query.contains("estado") || query.contains("avance") || query.contains("mi scooter") || query.contains("mi moto") -> {
                "🔧 **Consulta de Estado:**\n" +
                "¡Claro! En la parte superior de la aplicación, puedes registrarte o ingresar tu número de teléfono para ver en tiempo real el progreso de tu reparación (Recibido ➔ Diagnóstico ➔ En Reparación ➔ Control de Calidad ➔ Listo)."
            }
            query.contains("whatsapp") || query.contains("wassap") || query.contains("numero") || query.contains("contacto") -> {
                "💬 **Contacto por WhatsApp:**\n" +
                "Puedes enviar toda tu información técnica e imágenes de tu scooter o moto eléctrica haciendo clic en el botón **'Enviar a WhatsApp'** en la pantalla principal. Esto abrirá un canal directo con nuestro WhatsApp Business para una atención ultrarrápida."
            }
            else -> {
                "¡Entendido! Te comento que en **LITIO ENERGY** nos especializamos en la reparación técnica de scooters, motos y bicicletas eléctricas de todas las marcas (Xiaomi, Dualtron, Segway, Minimotors, etc.).\n\nTe recomiendo **registrar tu scooter/moto** en el formulario de la app o presionar el botón de **WhatsApp Business** para enviarnos los datos técnicos para tu cotización rápida."
            }
        }
    }
}
