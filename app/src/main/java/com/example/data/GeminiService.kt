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
                Eres Litio AI, el asistente virtual de diagnóstico y soporte técnico especializado de 'LITIO ENERGY'.
                
                REGLA DE ORO DE RESPUESTA DIRECTA:
                - Sé extremadamente directo, preciso y ve al grano de inmediato. Evita saludos largos, preámbulos de bienvenida informales o introducciones innecesarias.
                - Responde la pregunta del usuario de manera profesional, seria, técnica y directa en las primeras líneas de tu respuesta.
                - Entrega la solución, los pasos prácticos o la explicación técnica de forma concisa y estructurada usando viñetas breves.
                - Mantén un tono formal, profesional, técnico y serio en todo momento. No uses emojis excesivos ni informales, concéntrate en la precisión técnica y de seguridad.
                
                NUEVA REGLA CRÍTICA PARA ERRORES ESPECÍFICOS:
                - Si el usuario te pregunta por un código de error específico (por ejemplo: Error 21, Error 14, Error 15, Error 35, Error 18, Errores 11, 12, 13, etc.), debes enfocarte ÚNICAMENTE en explicar ese error específico de forma breve y directa. No le des una lista de todos los otros errores.
                - No le des muchas opciones o guías de desarmar o repararlo en casa. Explícale amablemente que NO debe manipularlo él mismo si no es personal técnico calificado, ya que puede agravar la falla o ponerse en riesgo de descarga o cortocircuito.
                - Aconséjale encarecidamente que lo traiga a los especialistas de Litio Energy para un diagnóstico profesional, seguro y garantizado, indicándole que puede registrar el vehículo en la app o usar el botón "Enviar WhatsApp".
                
                Cubre temas clave como:
                - Códigos de error comunes de scooters (por ejemplo, en Xiaomi: Error 14/15 en acelerador/freno, Error 18 en sensores Hall del motor, Error 21 en comunicación con el BMS de la batería).
                - Motor caliente o sobrecalentamiento: Explica detalladamente que esto suele deberse a pastillas de freno descalibradas que rozan el disco o tambor/zapata (generando resistencia mecánica), o a sobrecarga de peso o esfuerzo excesivo (solicita al usuario revisar las especificaciones técnicas del vehículo para saber cuánta carga máxima soporta). Indica dejarlo enfriar 30 minutos y verificar el giro libre de la rueda. Recomienda el servicio técnico si el problema persiste.
                - Fallas de batería y Autonomía (Por ejemplo: 48V y motor de 1000W): Explica de manera técnica pero sencilla que la autonomía (rango en kilómetros) no solo depende de los Voltios (V) y Watts (W) del motor, sino fundamentalmente de los Amperios-hora (Ah) de la batería, lo cual nos da los Watt-hora totales (Wh = V x Ah). Explica que un motor de 1000W consume bastante energía, por lo que a potencia máxima o subiendo pendientes la autonomía se reducirá considerablemente. Da un ejemplo práctico de cálculo: Si la batería es de 48V and 13Ah (624Wh), con un consumo promedio en terreno plano de 20Wh/km, daría aproximadamente unos 25 a 30 km de autonomía real. Si es de 48V 20Ah (960Wh), podría llegar a 40 o 45 km. Indica que factores como el peso del conductor, presión de llantas, velocidad y pendientes influyen directamente.
                - Conversión de Plomo-Ácido a Litio: Si un usuario pregunta sobre cambiar baterías de ácido plomo a litio, explícale que es una EXCELENTE decisión y detalla los porqués: 1) Reducción drástica de peso (hasta 70% menos peso, lo que mejora la velocidad y aceleración), 2) Vida útil de 4 a 5 veces mayor (el litio dura más de 2000 ciclos vs 300 de plomo), 3) Entrega de potencia constante sin caída de voltaje (no pierde velocidad a mitad de carga), y 4) Carga mucho más rápida y eficiente. Advierte que requiere cambiar de cargador a uno específico de litio y adecuar el espacio físico con soporte acolchado en Litio Energy.
                - Selección de Cargador y Amperaje: Si preguntan qué amperaje usar para un cargador (por ejemplo, para una batería de 72V 50Ah): Explica que la corriente recomendada es de 10% (0.1C) a 20% (0.2C) de los Amperios-hora (Ah) de la batería. Para una batería de 50Ah, lo ideal para cuidado diario (saludable) es 5A (tarda unas 10 horas) o 10A para carga rápida segura (tarda unas 5 horas). Advierte que superar 15A/20A puede recalentar o dañar las celdas o activar la protección del BMS. Para 72V nominales, el cargador debe ser específico para Litio de 72V (con voltaje máximo de salida de 84V para Li-ion o 87.6V para LiFePO4).
                - Sin Voltaje en la Batería de Litio: Si un usuario te pregunta por qué no sale voltaje, no marca voltaje o su batería está muerta, responde directamente indicando que las causas principales son: 1) Fusible de salida quemado (por un cortocircuito externo o sobrecorriente), 2) Falla en la BMS o que la BMS se ha bloqueado por seguridad (debido a sobredescarga o sobrecalentamiento), y 3) Celdas caídas, desbalanceadas o muertas que causan que la BMS corte la energía por protección para evitar accidentes.
                - Vehículo Eléctrico Prende pero NO Acelera: Explica que si enciende pero no avanza ni acelera, las 5 causas más probables son: 1) Sensor de freno (maneta de freno trabada o sensor de corte defectuoso), 2) Acelerador (sensor del gatillo de aceleración dañado/mojado o descolocado), 3) Cableado o conector del acelerador (roto o suelto), 4) Sensores Hall del motor defectuosos, o 5) Falla interna en la controladora (etapa de potencia).
                - Motor Trabado o Rueda Dura: Explica que si el motor se siente trabado magnéticamente o la rueda está dura (incluso apagada), se debe a cables de fases del motor sobrecalentados y derretidos en cortocircuito entre sí, o a transistores MOSFETs quemados dentro de la controladora.
                - Vehículo Eléctrico Apagado por Completo (No Prende): Explica que las causas directas son: 1) Batería sin voltaje de salida o descargada del todo, 2) Fusible térmico de la moto/vehículo quemado, 3) Chapa de encendido o llave defectuosa, 4) Pantalla/Display dañado o desconectado, o 5) Controladora quemada.
                - Códigos de Error Comunes en Ninebot y Xiaomi:
                  * Error 14 (Acelerador): Sensor del acelerador roto o mojado. Se suele solucionar destapando la pantalla y reconectando los cables internos.
                  * Error 15 (Freno): Fallo en el sensor de la maneta de freno por agua o desgaste.
                  * Error 21 (Batería): Problema de comunicación con la batería (BMS) por cortocircuito interno (requiere cambiar placa base de la batería).
                  * Error 35 (Bloqueo de serie): Común en Ninebot si fue robado o activado incorrectamente, o en Xiaomi por intentos fallidos de "hackeo" (firmware custom fallido). Requiere contactar al fabricante o servicio técnico autorizado.
                  * Errores 11, 12 y 13 (Motor): Falla en alguna de las tres fases eléctricas del motor. Requiere revisar el cableado físico.
                - Problemas mecánicos (frenos que chillan, pastillas gastadas, neumáticos pinchados, presión ideal de 45-50 PSI).
                - Daños por agua o prevención tras lluvias (¡advertir que nunca enciendan ni carguen un equipo mojado!).
                - Cómo Cuidar la Batería de Litio: Explica que para alargar la vida útil de la batería se debe: 1) Usar siempre su cargador original o uno certificado por expertos, 2) Mantener la carga idealmente entre 20% y 80% en el día a día, 3) Evitar por completo que la batería se descargue al 0% y se quede muerta, 4) Guardarla a media carga (aproximadamente 50% o 60%) si no la vas a usar por un tiempo prolongado, y 5) Protegerla siempre del calor extremo, la luz directa del sol y la humedad.
                - Uso de Cargador de Plomo-Ácido en Batería de Litio: Si te preguntan sobre usar un cargador de plomo-ácido en una batería de litio, tu respuesta debe comenzar de inmediato indicando que por regla general no se debe usar por tener algoritmos de carga completamente diferentes que pueden dañar la batería o provocar un incendio. Explica que aunque existen excepciones técnicas muy específicas con cargadores antiguos "tontos" bajo estricta supervisión, hacerlo de manera regular destruirá la vida útil de las celdas de litio. Enfatiza los riesgos (como el algoritmo incompatible y voltajes de desulfatación destructivos). Aconséjale de forma clara que no intente manipularlo o experimentar si no es personal técnico calificado, y que lo traiga con los especialistas de Litio Energy para adquirir el cargador específico de litio adecuado, registrando su vehículo o usando el botón "Enviar WhatsApp".
                
                Si te preguntan sobre el estado de su reparación, recuérdales amablemente que pueden consultar el progreso ingresando su número de teléfono en la sección 'CONSULTAR AVANCE' de la pantalla principal.
                
                Siempre habla en español. Mantén tus respuestas fáciles de entender, motivadoras, bien estructuradas con emojis simpáticos y viñetas claras.
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
                "Bienvenido a **LITIO ENERGY**. ⚡\n\nSoy el **Asistente de Diagnóstico Especializado (Litio AI)**. Estoy programado para realizar análisis técnicos, interpretar códigos de error y diagnosticar fallas en sistemas de litio, motores y electrónica de vehículos eléctricos. Por favor, describa detalladamente la anomalía o el código de error que presenta su vehículo para iniciar el diagnóstico."
            }
            query.contains("error 14") || query.contains("err 14") || query.contains("error14") -> {
                "🔍 **Error 14 (Fallo de Acelerador):**\n\n" +
                "• **Explicación:** Este código indica que el sensor de pasillo (Hall) del acelerador está dañado, desgastado, descolocado o le ha ingresado humedad.\n\n" +
                "⚠️ **Recomendación crítica:** Te aconsejamos **no intentar desarmar ni reparar el acelerador por tu cuenta**, ya que al no ser personal técnico calificado podrías dañar la pantalla, romper los cables internos o provocar un cortocircuito que queme la controladora.\n\n" +
                "🛠️ **¿Cómo lo solucionamos?** En **Litio Energy** contamos con repuestos originales de aceleradores y personal calificado para realizar el cambio de manera rápida y segura. Registra tu vehículo en la app o pulsa el botón **'Enviar a WhatsApp'** para traerlo a revisión técnica."
            }
            query.contains("error 15") || query.contains("err 15") || query.contains("error15") -> {
                "🔍 **Error 15 (Fallo de Freno):**\n\n" +
                "• **Explicación:** Ocurre cuando el sensor electromagnético de la maneta de freno no regresa correctamente a su posición, está desgastado o le ha ingresado agua.\n\n" +
                "⚠️ **Recomendación crítica:** Dado que los frenos son el sistema de seguridad más vital de tu vehículo, **te sugerimos encarecidamente no manipular ni intentar reparar los sensores tú mismo**. Una mala manipulación puede dejar el vehículo sin frenos o bloquearlo permanentemente.\n\n" +
                "🛠️ **¿Cómo lo solucionamos?** En **Litio Energy** somos expertos en calibrar y reemplazar sensores de freno de todas las marcas de manera segura. Registra tu vehículo en la app o pulsa **'Enviar a WhatsApp'** para agendar tu diagnóstico profesional."
            }
            query.contains("error 21") || query.contains("err 21") || query.contains("error21") -> {
                "🔍 **Error 21 (Problema de Comunicación de Batería):**\n\n" +
                "• **Explicación:** Indica que la placa controladora perdió comunicación de datos con el circuito inteligente (BMS) de la batería, comúnmente causado por un cortocircuito interno, pines desoldados o falla física en la BMS.\n\n" +
                "⚠️ **Recomendación crítica:** Las celdas de litio almacenan alta energía y son inflamables. **No intentes desarmar la batería ni tocar sus componentes internos bajo ninguna circunstancia**, ya que un mal contacto o herramienta metálica puede causar una explosión química. Solo personal calificado debe intervenirla.\n\n" +
                "🛠️ **¿Cómo lo solucionamos?** En **Litio Energy** abrimos los packs en un entorno controlado, diagnosticamos la BMS por software y reparamos a nivel de microcomponentes de forma 100% segura. Registra tu vehículo en la app o presiona **'Enviar a WhatsApp'** para que lo solucionemos."
            }
            query.contains("error 35") || query.contains("err 35") || query.contains("error35") -> {
                "🔍 **Error 35 (Bloqueo de Número de Serie):**\n\n" +
                "• **Explicación:** Indica un número de serie incorrecto o bloqueado en la controladora, usualmente debido a un cambio de placa no configurado o a un intento fallido de alteración de firmware ('hackeo').\n\n" +
                "⚠️ **Recomendación crítica:** No intentes forzar actualizaciones de software ni instalar programas de internet ('custom firmwares'), ya que podrías inhabilitar ('brickear') por completo las placas del vehículo.\n\n" +
                "🛠️ **¿Cómo lo solucionamos?** En **Litio Energy** contamos con software de diagnóstico y reprogramación oficial para registrar números de serie originales. Registra tu vehículo en la app o haz clic en **'Enviar a WhatsApp'** para reprogramarlo con total seguridad."
            }
            query.contains("error 18") || query.contains("err 18") || query.contains("error18") -> {
                "🔍 **Error 18 (Fallo de Sensores Hall del Motor):**\n\n" +
                "• **Explicación:** Ocurre cuando la controladora no puede leer los sensores de giro magnético internos del motor, impidiendo que acelere.\n\n" +
                "⚠️ **Recomendación crítica:** Desarmar el motor requiere herramientas de extracción magnética especiales. **Te recomendamos no abrirlo por tu cuenta**, ya que podrías romper el bobinado de cobre o dañar los potentes imanes de neodimio permanentes.\n\n" +
                "🛠️ **¿Cómo lo solucionamos?** En **Litio Energy** reemplazamos sensores Hall internos con repuestos de alta durabilidad y rehacemos el aislamiento técnico. Registra tu vehículo en la app o escríbenos al **WhatsApp** para agendar su reparación especializada."
            }
            query.contains("error 11") || query.contains("error 12") || query.contains("error 13") || query.contains("err 11") || query.contains("err 12") || query.contains("err 13") || query.contains("error11") || query.contains("error12") || query.contains("error13") -> {
                "🔍 **Errores 11, 12 y 13 (Falla de Fases del Motor):**\n\n" +
                "• **Explicación:** Indica una interrupción o corte eléctrico en alguna de las tres fases (cables amarillo, verde o azul) que conectan la controladora con el motor.\n\n" +
                "⚠️ **Recomendación crítica:** Estos cables transmiten altos picos de amperaje. **No intentes pelar o empalmar los cables tú mismo**, ya que un mal contacto o cortocircuito quemará instantáneamente los transistores de potencia de tu controladora.\n\n" +
                "🛠️ **¿Cómo lo solucionamos?** En **Litio Energy** revisamos minuciosamente el recorrido eléctrico, cambiamos conectores derretidos por terminales de alta temperatura y reparamos la controladora de forma técnica. Registra tu vehículo o presiona **'Enviar a WhatsApp'** para traerlo de inmediato."
            }
            query.contains("error") || query.contains("codigo") || query.contains("código") || query.contains("fallo") || query.contains("falla") || query.contains("led") || query.contains("pitido") || query.contains("ninebot") || query.contains("xiaomi") -> {
                "🔍 **Códigos de error comunes en Ninebot y Xiaomi:**\n\n" +
                "Conocer el origen del fallo te ayudará a saber si puedes arreglarlo tú mismo o si necesitas llevar tu patinete a un taller:\n\n" +
                "• **Error 14 (Acelerador):** El sensor del acelerador está roto o mojado. A veces se soluciona destapando la pantalla y reconectando los cables de los conectores internos.\n" +
                "• **Error 15 (Freno):** Indica un fallo en el sensor de la maneta de freno. Suele ocurrir por ingreso de agua o desgaste físico.\n" +
                "• **Error 21 (Batería):** Problema de comunicación con la batería (BMS). Puede ser un cortocircuito interno, cable de datos desconectado o que requiere cambiar/reparar la placa del BMS de la batería.\n" +
                "• **Error 35 (Bloqueo de serie):** Muy común en Ninebot cuando el vehículo es bloqueado o activado incorrectamente, o en Xiaomi por intentos de custom firmware ('hackeo') fallidos. Solo se arregla contactando al fabricante o con un servicio autorizado.\n" +
                "• **Errores 11, 12 y 13 (Motor):** Falla en alguna de las tres fases eléctricas de tu motor. Necesitas llevarlo a un técnico para revisar y soldar el cableado de fases.\n" +
                "• **Error 18 (Sensores Hall):** Los sensores internos que controlan el giro del motor no responden. Revisa si el motor gira suavemente.\n\n" +
                "🛠️ **¿Tu scooter marca un código de error?** ¡En **Litio Energy** somos especialistas en Xiaomi y Ninebot! Contamos con software de reprogramación, sensores Hall de repuesto y BMS originales. Registra tu vehículo o haz clic en **'Enviar a WhatsApp'** para solucionarlo de inmediato."
            }
            query.contains("no acelera") || query.contains("no avanza") || query.contains("no camina") || (query.contains("prende") && query.contains("acelera")) || (query.contains("enciende") && query.contains("acelera")) -> {
                "⚡ **Si tu vehículo eléctrico prende pero no acelera, las 5 causas más comunes son:**\n\n" +
                "• **1. Sensor de freno activo:** Muchas manetas de freno tienen un sensor que corta la corriente del motor por seguridad al frenar. Si la maneta no regresa bien a su posición por desgaste o agua, el sensor se queda pegado y bloquea la aceleración.\n" +
                "• **2. Fallo en el acelerador:** El sensor interno del gatillo de aceleración puede estar roto, descolocado, mojado o desgastado.\n" +
                "• **3. Cableado del acelerador:** Un cable del acelerador roto o conector flojo en la columna de dirección o dentro del display.\n" +
                "• **4. Sensores Hall del motor:** Falla en la lectura de posición de los imanes del motor.\n" +
                "• **5. Controladora defectuosa:** Daño interno en los transistores que mandan corriente al motor.\n\n" +
                "🛠️ **¿Qué hacer?** Deja de intentar acelerarlo para no agravar el problema. Registra tu vehículo en la app o pulsa **'Enviar a WhatsApp'** para que en **Litio Energy** realicemos un escaneo técnico preciso."
            }
            query.contains("no prende") || query.contains("no enciende") || query.contains("apagado") || query.contains("no prende nada") || query.contains("completamente muerto") -> {
                "🔌 **Si tu vehículo eléctrico está apagado por completo y no enciende nada, las causas principales son:**\n\n" +
                "• **1. Batería sin voltaje de salida:** Las celdas de tu batería están totalmente descargadas, el fusible de salida se quemó o la BMS se bloqueó por seguridad.\n" +
                "• **2. Fusible térmico o fusible general de la moto quemado:** El cortacorriente se fundió para proteger el sistema de un pico de consumo o cortocircuito.\n" +
                "• **3. Chapa de encendido / Interruptor de llave defectuoso:** La chapa está sulfatada, desconectada o rota físicamente.\n" +
                "• **4. Pantalla/Display dañado:** Si la pantalla se quemó o se mojó, no puede enviar la señal de encendido a la controladora.\n" +
                "• **5. Controladora quemada:** Si el cerebro del vehículo falló internamente, corta el suministro eléctrico general.\n\n" +
                "🛠️ **Próximo paso recomendado:** Se requiere un diagnóstico eléctrico con multímetro para hallar la interrupción del circuito. Registra el ingreso de tu vehículo en la app o escríbenos a nuestro **WhatsApp** para que los técnicos expertos de **Litio Energy** lo revisen de manera segura."
            }
            query.contains("motor duro") || query.contains("rueda dura") || query.contains("trabado") || query.contains("fase") || query.contains("face") || query.contains("fases") || query.contains("faces") -> {
                "⚙️ **Si tu rueda o motor se siente trabado magnéticamente, duro al girar, o hay sobrecalentamiento de cables:**\n\n" +
                "• **Cables de fases sobrecalentados/derretidos:** Los cables que llevan la energía de la controladora al motor (amarillo, azul, verde) se calientan tanto por esfuerzo excesivo o sobrecarga que funden su aislante plástico y hacen contacto entre sí. Esto produce un cortocircuito cerrado que frena la rueda magnéticamente por completo.\n" +
                "• **Problema en la controladora (Transistores quemados):** Los transistores MOSFETs de potencia de la controladora se quemaron en cortocircuito directo, bloqueando el libre giro de la rueda.\n\n" +
                "🛠️ **Recomendación crítica:** ¡No intentes forzar o rodar el vehículo así, o quemarás el motor completo! Registra tu vehículo en el app o escríbenos al **WhatsApp** para traerlo a **Litio Energy**. Realizamos rebobinados, cambio de cables de fase de alta temperatura y reparación de controladoras."
            }
            query.contains("agua") || query.contains("lluvia") || query.contains("mojado") || query.contains("humedad") || query.contains("charco") || query.contains("mojar") -> {
                "¡Ay no, el agua es sumamente peligrosa para la electrónica! 🌧️ Si pasaste por charcos o te agarró la lluvia, sigue estas recomendaciones urgentes:\n\n" +
                "• **Explicación:** El agua ingresa a la base e inicia un proceso de cortocircuito en la controladora o sulfatación en las celdas de la batería.\n" +
                "• **Solución en casa de inmediato:**\n" +
                "  1. **¡NO lo enciendas!** Y bajo ninguna circunstancia lo conectes al cargador.\n" +
                "  2. Si es posible, seca la superficie con un paño seco y déjalo inclinado en un lugar cálido y seco para que drene la humedad acumulada.\n\n" +
                "🛠️ **Próximo paso recomendado:** Si ya hiciste lo anterior, lo más seguro es realizarle un secado térmico profundo y baño químico ultrasónico en taller antes de que las celdas de litio se dañen permanentemente. Registra tu ingreso en la app o escríbenos a nuestro **WhatsApp** para traerlo de inmediato a **Litio Energy**."
            }
            query.contains("calien") || query.contains("temperatura") || query.contains("calienta") || query.contains("calentar") || query.contains("quemado") || query.contains("humo") -> {
                "🔥 **¡Atención! Si tu motor se calienta demasiado, esto es muy importante:**\n\n" +
                "• **Explicación:** El sobrecalentamiento del motor usualmente ocurre por dos razones principales:\n" +
                "  1. **Resistencia mecánica (Frenos):** Es muy común que las pastillas de freno estén descalibradas y estén haciendo presión constante contra el disco o el tambor de la zapata, obligando al motor a esforzarse el doble.\n" +
                "  2. **Sobrecarga o esfuerzo excesivo:** Conducir con exceso de peso o subir pendientes muy prolongadas. Te sugerimos revisar las características técnicas de tu vehículo para saber cuánta carga máxima puede soportar (normalmente entre 100 kg y 120 kg).\n\n" +
                "• **Solución en casa (Intenta esto primero):**\n" +
                "  1. **Deja enfriar:** Apaga el vehículo por completo y colócalo a la sombra durante al menos 30 a 45 minutos.\n" +
                "  2. **Verifica el giro libre:** Levanta la rueda del motor y gírala con la mano. Si no gira libremente o se frena de inmediato, las pastillas o la zapata están descalibradas y rozando.\n" +
                "  3. **Controla el peso:** Evita sobrecargar el vehículo o subir cuestas empinadas con pasajeros adicionales.\n\n" +
                "🛠️ **Próximo paso recomendado:** Si el motor sigue calentándose después de enfriarse y verificar que gira libremente, puede haber un problema de corriente o sensores internos. Te recomendamos registrar el ingreso de tu vehículo en esta app o pulsar **'Enviar a WhatsApp'** para que el servicio técnico especializado de **Litio Energy** realice un diagnóstico seguro y evite que el motor se queme por completo."
            }
            query.contains("motor") || query.contains("rueda") || query.contains("vibra") || query.contains("traba") || query.contains("sonido") -> {
                "¡Vaya! Que la rueda se trabe o vibre puede asustar, pero tiene solución. Aquí te explico:\n\n" +
                "• **Explicación:** Si la rueda está dura incluso apagada, es probable que haya un cruce en los transistores (MOSFETs) de la placa. Si vibra o tiembla al acelerar, suele ser un cable de fase suelto o desoldado.\n" +
                "• **Solución en casa:** Apaga el scooter. Intenta girar la rueda libremente para descartar si el disco de freno está muy ajustado o rozando físicamente.\n\n" +
                "⚙️ **Recomendación:** Forzar el motor en estas condiciones puede quemarlo por completo. Te sugerimos registrar tu scooter en el formulario de la app o pulsar **'Enviar a WhatsApp'** para agendar una revisión técnica en **Litio Energy**. ¡Hacemos rebobinados, cambio de sensores Hall y mantenimiento de rodajes!"
            }
            query.contains("autonom") || query.contains("rango") || query.contains("distanc") || query.contains("cuanto dur") || query.contains("cuánto dur") || query.contains("km") || query.contains("kilometr") || query.contains("kilómetr") -> {
                "🔋 **¡Excelente pregunta sobre la autonomía de tu vehículo! Aquí te explico cómo calcularlo y qué esperar:**\n\n" +
                "• **La regla de oro de la autonomía:**\n" +
                "Para saber cuántos kilómetros recorrerás, no solo necesitamos los **Voltios (V)** y los **Watts (W)** del motor; el dato más importante son los **Amperios-hora (Ah)** de tu batería.\n\n" +
                "La energía total de tu batería se mide en **Watt-hora (Wh)**, que se calcula así:\n" +
                "👉 `Voltios (V) x Amperios-hora (Ah) = Watt-hora (Wh)`\n\n" +
                "• **Caso de una batería de 48V y un motor de 1000W:**\n" +
                "Un motor de 1000W es muy potente y divertido, pero consume energía rápidamente. En promedio, en un scooter o bicicleta eléctrica se consumen entre **15 y 25 Watt-hora por cada kilómetro (Wh/km)** en terreno plano y velocidad moderada.\n\n" +
                "Aquí tienes los estimados según la capacidad (Ah) de tu batería de 48V:\n" +
                "  1. **Si tu batería es de 48V 10Ah (480 Wh):**\n" +
                "     • Autonomía estimada: **20 a 25 kilómetros** por carga.\n" +
                "  2. **Si tu batería es de 48V 13Ah (624 Wh):**\n" +
                "     • Autonomía estimada: **25 a 30 kilómetros** por carga.\n" +
                "  3. **Si tu batería es de 48V 15-18Ah (720 a 864 Wh):**\n" +
                "     • Autonomía estimada: **30 a 40 kilómetros** por carga.\n" +
                "  4. **Si tu batería es de 48V 20Ah (960 Wh):**\n" +
                "     • Autonomía estimada: **45 a 50 kilómetros** por carga.\n\n" +
                "• **Factores que reducen tu autonomía (¡Tenlos en cuenta!):**\n" +
                "  - **Peso total:** A mayor peso del conductor, el motor de 1000W exige más corriente y gasta más batería.\n" +
                "  - **Subidas/Pendientes:** Conducir en subidas consume hasta 3 veces más energía que en plano.\n" +
                "  - **Presión de llantas:** Si las llantas están bajas de aire (menos de 45 PSI), hay más rozamiento y la batería se agota mucho más rápido.\n" +
                "  - **Modo de conducción:** Ir siempre en modo 'Sport' o a máxima velocidad exprime la batería al límite.\n\n" +
                "🛠️ **¿Sientes que tu batería rinde mucho menos de lo normal?** Con el tiempo, las celdas de litio se desbalancean o pierden capacidad. ¡No te preocupes! En **Litio Energy** somos expertos en diagnóstico de baterías, balanceo de celdas por software y fabricación de packs a medida. Registra tu vehículo en la app o haz clic en **'Enviar a WhatsApp'** para agendar una prueba de capacidad real."
            }
            query.contains("plomo") || query.contains("acido") || query.contains("ácido") || query.contains("convers") || query.contains("cambiar bater") || query.contains("cambio de bat") -> {
                "🔋 **¡Cambiar tus baterías de plomo-ácido a Litio es la mejor decisión que puedes tomar! Aquí te explico detalladamente por qué:**\n\n" +
                "• **¿Por qué el Litio es infinitamente mejor que el Plomo-Ácido?**\n" +
                "  1. **Reducción drástica de peso (Hasta 70% menos):** Una batería de plomo de 48V puede pesar 16-20 kg, mientras que una equivalente de litio pesa solo unos 4-5 kg. Tu vehículo será mucho más ágil, subirá mejor las pendientes y frenará en menor distancia.\n" +
                "  2. **Vida útil 5 veces mayor:** El plomo-ácido solo dura entre 300 y 500 ciclos de carga (1 año promedio). El litio de alta calidad dura entre **1500 y 3000 ciclos** (¡de 5 a 8 años de vida útil!).\n" +
                "  3. **Potencia constante (Sin caída de velocidad):** Con plomo, a medida que la batería se descarga, el scooter pierde fuerza y velocidad (efecto 'voltaje sag'). Con el litio, mantendrás la potencia máxima estable hasta el último 10% de carga.\n" +
                "  4. **Carga rápida y eficiencia:** Se carga de 2 a 4 horas frente a las eternas 8 a 10 horas de las de plomo, consumiendo menos electricidad en tu hogar.\n\n" +
                "• **¿Qué se necesita para realizar el cambio? (Consideraciones importantes):**\n" +
                "  1. **Cambiar de cargador:** ¡Obligatorio! Nunca uses el cargador de plomo para una batería de litio; las curvas de voltaje son distintas y podrías dañarla o causar un accidente.\n" +
                "  2. **Adaptar el compartimiento:** Como el litio es más pequeño, quedará un espacio libre que hay que rellenar con soporte de espuma de alta densidad (EVA/neopreno) para que no rebote.\n" +
                "  3. **Verificar el voltaje:** Debe coincidir con el voltaje original de tu motor y controladora (ej. si usabas 4 baterías de plomo de 12V en serie = 48V, debes colocar un pack de litio de 48V).\n\n" +
                "🛠️ **Próximo paso recomendado:** En **Litio Energy** diseñamos y fabricamos packs de litio a medida con celdas originales y circuitos BMS inteligentes de protección de última generación. Nosotros nos encargamos de todo: instalación limpia, ajuste del compartimiento y te entregamos el cargador adecuado. Registra tu vehículo en la app o presiona **'Enviar a WhatsApp'** para cotizar tu conversión personalizada y revivir tu vehículo con energía moderna."
            }
            query.contains("cargador") || query.contains("amper") || query.contains("ampere") || query.contains("amp") || query.contains("corriente") -> {
                "⚡ **Recomendación técnica para el cargador de tu batería:**\n\n" +
                "• **Para tu batería de 72V y 50Ah (o cualquier capacidad):**\n" +
                "  1. **El Amperaje Ideal (Corriente de carga):** La regla de oro para cuidar la vida útil de tus celdas de litio es cargar a un ritmo de **0.1C a 0.2C** (es decir, entre el 10% y 20% de los Ah totales de tu batería):\n" +
                "     - **Carga saludable / diaria (5 Amperios):** Un cargador de **5A** es el más recomendado para 50Ah. Al cargar de forma lenta, la batería no calienta, protegiendo las celdas de litio para que duren mucho más tiempo. Tarda unas 10 horas de 0 a 100%.\n" +
                "     - **Carga rápida segura (10 Amperios):** Un cargador de **10A** es ideal si requieres velocidad para tu batería de 50Ah. Tardará aproximadamente unas 5 horas de 0 a 100% de forma segura.\n" +
                "     - **⚠️ Máximo permitido:** Evita usar cargadores de más de **15A o 20A** a menos que tu pack y tu BMS de protección inteligente estén específicamente fabricados para carga ultra rápida. El exceso de corriente calienta las celdas y puede dañar la química de litio o activar el apagado de emergencia del BMS.\n\n" +
                "  2. **El Voltaje del Cargador:** Para una batería con voltaje nominal de **72V**, el cargador debe ser específico de Litio de 72V. Su voltaje máximo de salida final de carga completa debe ser de **84V** (para celdas Li-Ion estándar de 20 series - 20S) o de **87.6V** (para celdas LiFePO4 de 24 series - 24S).\n\n" +
                "🛠️ **¿Buscas el cargador ideal calibrado?** En **Litio Energy** importamos, calibramos y garantizamos cargadores inteligentes de última generación con ventilación integrada y autoapagado protector. ¡Registra tu vehículo en la app o pulsa **'Enviar a WhatsApp'** para cotizar tu cargador personalizado hoy mismo!"
            }
            query.contains("sin volt") || query.contains("no sale volt") || query.contains("no bota volt") || query.contains("no marca volt") || query.contains("cero volt") || query.contains("0v") || query.contains("bateria muert") || query.contains("batería muert") || query.contains("no tiene volt") || query.contains("no bota corr") || query.contains("sin corr") -> {
                "⚡ **Si tu batería de litio no tiene salida de voltaje o está 'muerta', estas son las 3 causas principales:**\n\n" +
                "• **1. Fusible de salida quemado:**\n" +
                "  - **Explicación:** Es la protección física de tu batería. Si hubo un chispazo al conectar el cargador, un cortocircuito en el puerto de carga o un sobreesfuerzo de la controladora, el fusible se quema de inmediato para proteger las celdas.\n" +
                "  - **Solución:** Requiere desarmar con seguridad el pack para reemplazar el fusible dañado por uno del mismo amperaje (típicamente de 30A a 40A).\n\n" +
                "• **2. Bloqueo o Falla en la BMS (Sistema de Gestión de Batería):**\n" +
                "  - **Explicación:** La BMS es el cerebro de la batería. Corta la salida de voltaje automáticamente por seguridad si detecta sobredescarga, cortocircuito, o sobrecalentamiento.\n" +
                "  - **Solución:** Conectar el cargador original durante unos minutos a veces reactiva/desbloquea la BMS. Si no responde, la BMS podría estar dañada físicamente y requerir cambio.\n\n" +
                "• **3. Celdas caídas o desbalanceadas:**\n" +
                "  - **Explicación:** Si un grupo de celdas de litio cae por debajo de su voltaje mínimo seguro (usualmente menor a 2.5V o 3V por celda), la BMS bloquea permanentemente la salida de corriente para evitar que la batería se incendie al cargarse.\n" +
                "  - **Solución:** Se debe abrir el pack para medir grupo por grupo con un multímetro, balancear o reemplazar las celdas muertas y reprogramar/reactivar la BMS.\n\n" +
                "⚠️ **Recomendación crítica de seguridad:** ¡Nunca abras una batería de litio en casa! Un mal golpe o cortocircuito accidental con herramientas metálicas puede provocar una explosión o incendio químico difícil de apagar.\n\n" +
                "🛠️ En **Litio Energy** somos expertos en diagnóstico avanzado, reemplazo de BMS inteligentes, cambio de fusibles y reactivación segura de packs de litio. Registra tu vehículo en la app o presiona **'Enviar a WhatsApp'** para cotizar la reparación técnica de tu batería."
            }
            query.contains("cuidar mi bater") || query.contains("cuidar la bater") || query.contains("cuidado de bater") || query.contains("cuidar bateria") || query.contains("cuidar batería") || query.contains("vida de mi bater") || query.contains("alargar la vida") -> {
                "🔋 **Cómo cuidar tu batería de litio para alargar su vida útil:**\n\n" +
                "Para maximizar la durabilidad y rendimiento de tu batería de litio, sigue siempre estas pautas fundamentales:\n\n" +
                "• **1. Usa su cargador original:** Utiliza siempre el cargador provisto o uno certificado específicamente calibrado para el voltaje y amperaje de tu batería.\n" +
                "• **2. Rango de carga óptimo (20% a 80%):** Evita cargarla siempre al 100% si no vas a realizar un viaje largo inmediato, y mantén la carga preferiblemente por encima del 20% para evitar ciclos de descarga profunda.\n" +
                "• **3. Evita descargas completas:** No permitas que el vehículo se apague por completo por falta de energía. Descargar el litio a 0V puede dañar químicamente las celdas o bloquear la BMS.\n" +
                "• **4. Almacenamiento a media carga:** Si vas a guardar tu vehículo por semanas o meses, déjalo con una carga de entre 50% y 60% en un lugar fresco. Nunca la guardes completamente descargada ni al 100%.\n" +
                "• **5. Protégela de temperaturas y humedad:** Evita dejar el vehículo bajo el sol intenso de la tarde o en lugares muy húmedos. La temperatura óptima de funcionamiento y almacenamiento es entre 15°C y 25°C.\n\n" +
                "🛠️ **¿Tu batería dura muy poco o no retiene carga?** Con el tiempo, las celdas se desbalancean de forma natural. En **Litio Energy** somos especialistas en balanceo, reacondicionamiento y fabricación de baterías a medida de alta calidad. Registra tu vehículo en la app o pulsa **'Enviar a WhatsApp'** para que un especialista la evalúe."
            }
            (query.contains("cargador") && (query.contains("plomo") || query.contains("acido") || query.contains("ácido"))) || query.contains("cargador de plomo") || query.contains("cargador plomo") -> {
                "🛑 **No, por regla general no debes usar un cargador de ácido-plomo para cargar una batería de litio, ya que tienen algoritmos de carga completamente diferentes y esto puede dañar la batería o provocar un incendio.**\n\n" +
                "Aunque existen excepciones técnicas muy específicas con ciertos cargadores antiguos 'tontos' bajo estricta supervisión, hacerlo de manera regular destruirá la vida útil de tus celdas de litio.\n\n" +
                "⚠️ **Recomendación crítica de seguridad:** Al no ser personal técnico calificado, **te sugerimos encarecidamente no manipular, conectar ni intentar cargar tus equipos con cargadores incompatibles**, ya que podrías provocar un cortocircuito grave o dañar irremediablemente el BMS de tu batería.\n\n" +
                "🛠️ **¿Necesitas el cargador correcto?** No pongas en riesgo tu seguridad ni tu equipo. Trae tu vehículo a los especialistas de **Litio Energy** para que te brindemos el cargador inteligente de litio adecuado y certificado para tu pack. Registra tu vehículo en la app o pulsa **'Enviar a WhatsApp'** para recibir asesoría experta de inmediato."
            }
            query.contains("bateria") || query.contains("batería") || query.contains("carga") || query.contains("litio") -> {
                "🔋 **¡Hablemos de la energía de tu vehículo! Si tu batería no carga o dura muy poco, esto es lo que debes saber:**\n\n" +
                "• **Explicación:** Las celdas de litio pueden desbalancearse, o el circuito de protección inteligente (BMS) puede bloquearse por seguridad tras una descarga extrema o sobrecalentamiento.\n" +
                "• **Solución en casa:**\n" +
                "  1. Deja que tu vehículo repose unos 15 a 20 minutos antes de cargarlo (nunca lo cargues inmediatamente después de usarlo, ya que está caliente).\n" +
                "  2. Verifica que el cargador encienda su luz led verde al enchufarse solo a la pared, y que cambie a roja al conectarlo al vehículo.\n\n" +
                "⚡ **¿Aún no responde?** Las baterías de litio son delicadas y altamente inflamables; por favor, no intentes abrirla en casa. Registra tu vehículo en el app o escríbenos por WhatsApp para que los técnicos especializados de **Litio Energy** evalúen y balanceen tus celdas de manera segura."
            }
            query.contains("freno") || query.contains("pastilla") || query.contains("disco") || query.contains("frenar") -> {
                "🛑 **¡Tu seguridad es lo más importante! Si los frenos hacen ruidos o no detienen bien tu vehículo:**\n\n" +
                "• **Explicación:** Los chillidos molestos suelen ser por pastillas de freno sucias, desgastadas o discos contaminados con grasa y polvo de la calle.\n" +
                "• **Solución en casa:** Limpia con cuidado el disco metálico de freno usando un paño limpio humedecido con alcohol isopropílico. Asegúrate de que no haya aceite en la pinza.\n\n" +
                "🔧 **Recomendación:** Si tras limpiarlo la respuesta de frenado sigue deficiente o larga, te aconsejamos registrar el ingreso de tu vehículo en la app o escribirnos por WhatsApp. ¡En **Litio Energy** calibramos calipers, purgamos sistemas hidráulicos y cambiamos pastillas cerámicas en minutos!"
            }
            query.contains("pinche") || query.contains("llanta") || query.contains("neumatico") || query.contains("neumático") || query.contains("aire") -> {
                "🚲 **¡Los pinchazos son baches en el camino, pero tienen solución rápida!**\n\n" +
                "• **Explicación:** En llantas con cámara de aire, andar con presión baja causa pinchazos por pellizco interno al pasar sobre piedras o baches.\n" +
                "• **Solución en casa:** Intenta mantener siempre la presión de tus llantas entre 45 y 50 PSI para scooters. Revísalas cada semana.\n\n" +
                "🛠️ **¿Ya se desinfló del todo?** Si el neumático se pincha, requiere cambio de cámara de aire. Registra tu vehículo en la app o escríbenos a nuestro WhatsApp. ¡En **Litio Energy** realizamos el cambio express de llantas y cámaras, o te instalamos llantas sólidas antipinchazos para que te olvides de inflarlas para siempre!"
            }
            query.contains("precio") || query.contains("costo") || query.contains("cuanto") || query.contains("cuánto") || query.contains("tarifa") -> {
                "💵 **Nuestras tarifas transparentes en Litio Energy:**\n\n" +
                "• **Diagnóstico de fallas:** S/. 20 (¡Totalmente Gratis si decides realizar el trabajo con nosotros!).\n" +
                "• **Cambio de neumático/cámara:** S/. 30 - 50\n" +
                "• **Mantenimiento preventivo completo:** S/. 80 - 120 (Ajuste general, lubricación y limpieza).\n\n" +
                "¡Si ya has tratado de resolver tu falla y necesitas un presupuesto cerrado, registra tu equipo en la pantalla principal o envíanos un WhatsApp para cotizarte!"
            }
            query.contains("estado") || query.contains("avance") || query.contains("mi scooter") || query.contains("mi moto") -> {
                "🔧 **¿Quieres saber cómo va tu Consentido?**\n\n" +
                "• **Explicación:** En nuestro taller, cada reparación pasa por 5 etapas claras para garantizar la máxima calidad técnica.\n" +
                "• **Solución en casa:** Ingresa tu número de teléfono registrado en la sección **'CONSULTAR AVANCE'** en la parte superior de la pantalla principal para ver el progreso actual en tiempo real (Recibido ➔ Diagnóstico ➔ En Reparación ➔ Control de Calidad ➔ Listo).\n\n" +
                "Si tienes alguna consulta adicional o quieres contactar directamente a tu técnico asignado, ¡puedes pulsar el botón **'Enviar a WhatsApp'**!"
            }
            query.contains("whatsapp") || query.contains("wassap") || query.contains("numero") || query.contains("contacto") -> {
                "💬 **¿Quieres conversar directamente con soporte técnico?**\n\n" +
                "¡Por supuesto! Solo pulsa el botón verde **'Enviar a WhatsApp'** en la pantalla principal. Esto armará automáticamente un mensaje con tus datos registrados para agendar tu cita y resolver tu caso de forma rápida."
            }
            else -> {
                "¡Entendido! Me encanta ayudarte. 😊 En **LITIO ENERGY** somos expertos apasionados en dar solución a scooters, bicicletas y motos eléctricas de todas las marcas (Xiaomi, Segway, Dualtron, etc.).\n\nSi estás presentando un problema, cuéntame los detalles para explicarte qué puede ser y qué pasos sencillos puedes probar en casa de forma segura. Y recuerda que si necesitas manos expertas, siempre puedes registrar tu vehículo en esta app para que lo revisemos de inmediato en nuestro taller especializado."
            }
        }
    }

    suspend fun getTechnicianChatResponse(
        userQuery: String,
        clientEntity: ClientEntity,
        chatHistory: List<Pair<String, Boolean>>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY" || apiKey == "GEMINI_API_KEY") {
            return@withContext "Hola ${clientEntity.name}, he recibido tu consulta sobre tu ${clientEntity.vehicleType} ${clientEntity.vehicleBrand}. El estado actual es ${clientEntity.status} (${clientEntity.progress}%). Un técnico especializado de la sede ${clientEntity.sede} revisará tu mensaje a la brevedad."
        }

        try {
            val historyBuilder = StringBuilder()
            chatHistory.takeLast(6).forEach { (text, isUser) ->
                val role = if (isUser) "user" else "model"
                val safeText = text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r")
                historyBuilder.append("""{"role": "$role", "parts": [{"text": "$safeText"}]},""")
            }

            val systemInstructionText = """
                Eres el Técnico Especializado de Servicio Técnico Litio Energy asignado a la sede ${clientEntity.sede}.
                Estás respondiendo directamente al cliente en el CHAT DIRECTO DE LA APLICACIÓN para su vehículo registrado:
                - Cliente: ${clientEntity.name}
                - Vehículo: ${clientEntity.vehicleType} ${clientEntity.vehicleBrand} ${clientEntity.vehicleModel}
                - Serie: ${clientEntity.vehicleSerialNumber}
                - Falla Reportada: ${clientEntity.problemDescription}
                - Estado Actual de Reparación: ${clientEntity.status} (${clientEntity.progress}%)
                - Notas Técnicas Actuales: ${clientEntity.technicianNotes}
                - Fecha estimada de entrega: ${clientEntity.estimatedCompletionDate}
                - Costo estimado: S/. ${clientEntity.estimatedCost}

                INSTRUCCIONES:
                - Responde de manera sumamente atenta, profesional y directa a la consulta específica del cliente.
                - Refiérete a los datos específicos de su vehículo o reparación cuando sea oportuno.
                - Firma tus mensajes amablemente como 'Atte. Servicio Técnico Litio Energy (${clientEntity.sede})'.
                - Mantén un tono técnico pero muy servicial y tranquilizador.
            """.trimIndent()

            val safeInstruction = systemInstructionText.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r")
            val safePrompt = userQuery.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r")

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
                    "Hola ${clientEntity.name}, hemos recibido tu mensaje. Tu ${clientEntity.vehicleBrand} ${clientEntity.vehicleModel} se encuentra en estado '${clientEntity.status}' (${clientEntity.progress}%). Nuestro equipo técnico en ${clientEntity.sede} te atenderá en breve."
                }
            }
        } catch (e: Exception) {
            "Hola ${clientEntity.name}, hemos registrado tu mensaje en el historial técnico. Tu ${clientEntity.vehicleBrand} está en estado '${clientEntity.status}'. Te informaremos cualquier actualización."
        }
    }
}
