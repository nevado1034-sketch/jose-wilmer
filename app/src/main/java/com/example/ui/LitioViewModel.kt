package com.example.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.ClientEntity
import com.example.data.ClientRepository
import com.example.data.GeminiService
import com.example.data.GoogleSheetsService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class LitioViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ClientRepository
    
    // UI state states
    var isAdminMode by mutableStateOf(false)
    var isClientRegistered by mutableStateOf(false)
    var trackedClient by mutableStateOf<ClientEntity?>(null)
    
    // Registration form states
    var regName by mutableStateOf("")
    var regPhone by mutableStateOf("")
    var regDni by mutableStateOf("")
    var regEmail by mutableStateOf("")
    var regVehicleType by mutableStateOf("Scooter") // "Scooter", "Bicicleta", "Moto", "Otros"
    var regVehicleBrand by mutableStateOf("")
    var regVehicleModel by mutableStateOf("")
    var regVehicleSerial by mutableStateOf("")
    var regProblem by mutableStateOf("")
    var regSede by mutableStateOf("Litio Surco") // "Litio Surco", "Litio San Borja", "Litio San Isidro", "Litio Lince"
    
    // Status tracking query state
    var lookupPhoneQuery by mutableStateOf("")
    var lookupDniQuery by mutableStateOf("")
    var lookupError by mutableStateOf<String?>(null)
    
    private val prefs = application.getSharedPreferences("litio_settings", android.content.Context.MODE_PRIVATE)

    // WhatsApp configuration
    var whatsappNumber by mutableStateOf(prefs.getString("whatsapp_number", "+51975925094") ?: "+51975925094")

    // Google Sheets Webhook URL
    var googleSheetsWebhookUrl by mutableStateOf(
        prefs.getString("google_sheets_url", "")?.let { savedUrl ->
            val oldUrl1 = "https://script.google.com/macros/s/AKfycbzWYdRUsL6evWEocpaGAKr1NGWeDvABPK5K2jNZsZ16ZmIGV-D-njhitSLf6n0AAfaj-A/exec"
            val oldUrl2 = "https://script.google.com/macros/s/AKfycbwKqz9hasm9wHvNy6jDxB4yEqRiuf8-4ZlkmeZ0WU99PZDkpzdgmWOzV22Wjy-EmDXcNg/exec"
            val oldUrl3 = "https://script.google.com/macros/s/AKfycbykiBraleD0Xm4nDdPRt0qgnZJm6zUhpK_8gPsfI1Urmwh9wOeo_FNFrVz9ucBMydAHmA/exec"
            val oldUrl4 = "https://script.google.com/macros/s/AKfycbzLD8p9HMDEtclUT0W6rN631JY6PXY0L39xF4YJQ-xrsMWUuDzyb3gsodN6RWRMWDyf/exec"
            val oldUrl5 = "https://script.google.com/macros/s/AKfycbzH5J4t_SzYcYv2qncLIHVh9oMGt6m0OBa1UPhzIRdH2ZGrZ77Ka2qDc4-0p_3UreZQ/exec"
            val oldUrl6 = "https://script.google.com/macros/s/AKfycbzjAcrLiW5A7fDBT8tMPoHKSUbuNIhltZGlGlohbP4Ra4IWhUMz44OTJ9fIVu2TbHOkjA/exec"
            val oldUrl7 = "https://script.google.com/macros/s/AKfycbyHolIhIsJjsW-e3nwUnL134kO61rDxfDaA87X37Z5CBVRTKlq_qcS96oqIx2AVBAMqzQ/exec"
            val oldUrl8 = "https://script.google.com/macros/s/AKfycbxpJRU9okNyJTgvfrDncZ6SoEh8EZLcKFnthVjaqi1ng9NvVQBPzGVGyOZfdX-dhCqANA/exec"
            val oldUrl9 = "https://script.google.com/macros/s/AKfycbwFTTzMPPJ4ZkNFDDMRMmtrRWKY1nrzDl3_k7HQP0sLFetz2LJHnti3XRvNjaKlNh1Dhw/exec"
            val oldUrl10 = "https://script.google.com/macros/s/AKfycbxtrvSSLL5JvMqRha8HgR9Vi0DrZKjBv_y61njTdN0RVgRF8p9wiSKsrwnYxNvOgvLrkw/exec"
            val oldUrl11 = "https://script.google.com/macros/s/AKfycbzaHzMBGXMFtpkdp6iz6dMu5ErwC4y9EvlNhrUAMRy_td48QzqmrNmXyWg6fZSCyK1Gqg/exec"
            val oldUrl12 = "https://script.google.com/macros/s/AKfycbyq5e1ghRfY4zpLKUdi2Qm0DekJc5Sx6G1caNJ55bylzzjKfbqKOn1Mw60zewMEYLaxFQ/exec"
            val oldUrl13 = "https://script.google.com/macros/s/AKfycbzH9cteSYnMUaxY0Gw-6XxkCc91-Ep088TUWF8W0yzojCWBd2JQDeh8q34fW_aSbT-L_A/exec"
            val oldUrl14 = "https://script.google.com/macros/s/AKfycbyu-WFG20qmIfI1dxp4fqbKLSXsoAZahzRLISdJggXFJ9_kB0HaUqe7Pfx4W79UVQCw-A/exec"
            val targetUrl = "https://script.google.com/macros/s/AKfycbxNwzO7bX0EyZdUspdIM-9Sx-TjY2LKySZCbGvSl42rqQ5Gtq6h0gn6D6oFVMfghunaDQ/exec"
            if (savedUrl.isBlank() || savedUrl == oldUrl1 || savedUrl == oldUrl2 || savedUrl == oldUrl3 || savedUrl == oldUrl4 || savedUrl == oldUrl5 || savedUrl == oldUrl6 || savedUrl == oldUrl7 || savedUrl == oldUrl8 || savedUrl == oldUrl9 || savedUrl == oldUrl10 || savedUrl == oldUrl11 || savedUrl == oldUrl12 || savedUrl == oldUrl13 || savedUrl == oldUrl14) {
                prefs.edit().putString("google_sheets_url", targetUrl).apply()
                targetUrl
            } else {
                savedUrl
            }
        } ?: "https://script.google.com/macros/s/AKfycbxNwzO7bX0EyZdUspdIM-9Sx-TjY2LKySZCbGvSl42rqQ5Gtq6h0gn6D6oFVMfghunaDQ/exec"
    )

    var isSyncingSheets by mutableStateOf(false)
    var syncStatusMessage by mutableStateOf<String?>(null)

    fun updateWhatsappNumber(number: String) {
        whatsappNumber = number
        prefs.edit().putString("whatsapp_number", number).apply()
    }

    fun updateGoogleSheetsWebhookUrl(url: String) {
        googleSheetsWebhookUrl = url
        prefs.edit().putString("google_sheets_url", url).apply()
        
        val trimmed = url.trim()
        if (trimmed.contains("docs.google.com/spreadsheets")) {
            syncStatusMessage = "⚠️ Has ingresado el enlace del documento. Debes crear un Apps Script en 'Extensiones' > 'Apps Script' y pegar su URL de Web App (comienza con script.google.com)."
        } else if (trimmed.isNotBlank() && !trimmed.contains("script.google.com/macros")) {
            syncStatusMessage = "⚠️ Asegúrate de pegar la URL del Web App de Apps Script (debe comenzar con script.google.com)."
        } else {
            syncStatusMessage = null
        }
    }

    fun resetToDefaultGoogleSheetsUrl() {
        val targetUrl = "https://script.google.com/macros/s/AKfycbxNwzO7bX0EyZdUspdIM-9Sx-TjY2LKySZCbGvSl42rqQ5Gtq6h0gn6D6oFVMfghunaDQ/exec"
        updateGoogleSheetsWebhookUrl(targetUrl)
        syncStatusMessage = "✅ URL restablecida a la de Google Sheets por defecto."
    }
    
    // Admin screen states
    var adminSearchQuery by mutableStateOf("")
    var adminStatusFilter by mutableStateOf("Todos") // "Todos", "Recibido", "Diagnóstico", "En Reparación", "Control de Calidad", "Listo para Retirar"
    var isEditingClientDialogVisible by mutableStateOf(false)
    var isAddingClientDialogVisible by mutableStateOf(false)
    var clientBeingEdited by mutableStateOf<ClientEntity?>(null)
    
    // Chat states
    val chatMessages = mutableStateListOf<ChatMessage>()
    var chatInputText by mutableStateOf("")
    var isChatLoading by mutableStateOf(false)

    init {
        val clientDao = AppDatabase.getDatabase(application).clientDao()
        repository = ClientRepository(clientDao)
        
        // Add greeting to chat
        addSystemGreeting()
        
        // Prepopulate database if empty
        viewModelScope.launch {
            repository.allClients.collect { list ->
                if (list.isEmpty()) {
                    prepopulateSampleData()
                }
            }
        }
    }

    // Expose all clients reactively, combining with search queries and status filters
    private val _adminSearchQueryFlow = MutableStateFlow("")
    private val _adminStatusFilterFlow = MutableStateFlow("Todos")

    val clientsList: StateFlow<List<ClientEntity>> = combine(
        repository.allClients,
        _adminSearchQueryFlow,
        _adminStatusFilterFlow
    ) { clients, query, filter ->
        clients.filter { client ->
            val matchesSearch = client.name.contains(query, ignoreCase = true) ||
                    client.phone.contains(query, ignoreCase = true) ||
                    client.vehicleBrand.contains(query, ignoreCase = true) ||
                    client.vehicleModel.contains(query, ignoreCase = true) ||
                    client.vehicleSerialNumber.contains(query, ignoreCase = true)
            
            val matchesFilter = filter == "Todos" || client.status.equals(filter, ignoreCase = true)
            
            matchesSearch && matchesFilter
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun updateAdminFilters(query: String, filter: String) {
        adminSearchQuery = query
        adminStatusFilter = filter
        _adminSearchQueryFlow.value = query
        _adminStatusFilterFlow.value = filter
    }

    private suspend fun prepopulateSampleData() {
        val sample1 = ClientEntity(
            name = "Juan Pérez",
            phone = "987654321",
            dni = "43567812",
            email = "juan.perez@email.com",
            vehicleType = "Scooter",
            vehicleBrand = "Xiaomi",
            vehicleModel = "Pro 2",
            vehicleSerialNumber = "XMY-9482-A",
            problemDescription = "Falla en batería, no carga más de 40% de autonomía.",
            status = "En Reparación",
            progress = 60,
            technicianNotes = "Celdas balanceadas con éxito. Reemplazando termistor defectuoso en el BMS de la batería.",
            estimatedCost = 120.00,
            estimatedCompletionDate = "05/07/2026"
        )
        val sample2 = ClientEntity(
            name = "María Rodríguez",
            phone = "955112233",
            dni = "72194853",
            email = "maria.rod@email.com",
            vehicleType = "Bicicleta",
            vehicleBrand = "Trek",
            vehicleModel = "Powerfly 4",
            vehicleSerialNumber = "TRK-928-11",
            problemDescription = "Frenos hidráulicos se sienten largos y pastillas muy gastadas.",
            status = "Control de Calidad",
            progress = 85,
            technicianNotes = "Purgado completo de frenos Shimano e instalación de pastillas cerámicas premium. Realizando pruebas de frenado en pendiente.",
            estimatedCost = 90.00,
            estimatedCompletionDate = "04/07/2026"
        )
        val sample3 = ClientEntity(
            name = "Carlos Mendoza",
            phone = "912345678",
            dni = "09823415",
            email = "carlos.men@email.com",
            vehicleType = "Moto",
            vehicleBrand = "Super Soco",
            vehicleModel = "TC Max",
            vehicleSerialNumber = "SS-TC-00482",
            problemDescription = "No enciende nada, la pantalla LCD se queda totalmente apagada tras lluvia.",
            status = "Diagnóstico",
            progress = 25,
            technicianNotes = "Verificando cableado principal de potencia. Fusible térmico de la controladora principal está OK. Desarmando panel para chequear humedad.",
            estimatedCost = 280.00,
            estimatedCompletionDate = "08/07/2026"
        )
        repository.insertClient(sample1)
        repository.insertClient(sample2)
        repository.insertClient(sample3)
    }

    private fun addSystemGreeting() {
        chatMessages.clear()
        chatMessages.add(
            ChatMessage(
                text = "¡Hola! Soy **LitioBot**, tu experto en micromovilidad eléctrica. ¿Tienes alguna pregunta sobre tu vehículo eléctrico? Cualquier problema solo consúltame para ayudarte. ⚡",
                isUser = false
            )
        )
    }

    // Client registration
    fun registerClient() {
        if (regName.isBlank() || regPhone.isBlank() || regVehicleBrand.isBlank() || regVehicleModel.isBlank()) {
            return
        }
        
        viewModelScope.launch {
            val newClient = ClientEntity(
                name = regName,
                phone = regPhone,
                dni = regDni,
                email = regEmail.ifBlank { "no-email@litio.com" },
                vehicleType = regVehicleType,
                vehicleBrand = regVehicleBrand,
                vehicleModel = regVehicleModel,
                vehicleSerialNumber = regVehicleSerial.ifBlank { "Otros" },
                problemDescription = regProblem.ifBlank { "Revisión general preventiva" },
                status = "Recibido",
                progress = 10,
                technicianNotes = "Vehículo registrado en taller. Pendiente de ingreso a bahía de diagnóstico.",
                estimatedCost = 0.00,
                estimatedCompletionDate = "Pendiente de diagnóstico",
                sede = regSede
            )
            val generatedId = repository.insertClient(newClient)
            val clientWithId = newClient.copy(id = generatedId.toInt())
            trackedClient = clientWithId
            isClientRegistered = true
            
            syncToGoogleSheets(clientWithId)
            
            // Auto add note in chat
            chatMessages.add(
                ChatMessage(
                    text = "¡Excelente, ${clientWithId.name}! He registrado tu **${clientWithId.vehicleType} ${clientWithId.vehicleBrand} ${clientWithId.vehicleModel}** con éxito en la sede **${clientWithId.sede}** de Litio Energy. Su estado actual es **${clientWithId.status}** (10%).\n\n¿Quieres saber qué revisaremos en el diagnóstico inicial o necesitas consultar algo más?",
                    isUser = false
                )
            )

            // Clear form inputs automatically
            regName = ""
            regPhone = ""
            regDni = ""
            regEmail = ""
            regVehicleType = "Scooter"
            regVehicleBrand = ""
            regVehicleModel = ""
            regVehicleSerial = ""
            regProblem = ""
            regSede = "Litio Surco"
        }
    }

    // Client lookup by phone
    fun lookupClientByPhone() {
        if (lookupPhoneQuery.isBlank()) {
            lookupError = "Por favor ingresa un número de celular."
            return
        }
        viewModelScope.launch {
            val client = repository.getClientByPhone(lookupPhoneQuery)
            if (client != null) {
                trackedClient = client
                isClientRegistered = true
                lookupError = null
                
                // Greeting updated in chat
                chatMessages.add(
                    ChatMessage(
                        text = "¡Hola de nuevo, ${client.name}! He cargado los datos de tu servicio técnico. Tu **${client.vehicleType} ${client.vehicleBrand}** está en estado: **${client.status}** (${client.progress}% de avance).\n\nPregúntame lo que desees sobre la reparación o haz clic en el botón de WhatsApp.",
                        isUser = false
                    )
                )
            } else {
                lookupError = "No se encontró ningún vehículo registrado con ese número celular."
            }
        }
    }

    // Client lookup by DNI or Carnet de Extranjería (C.E.)
    fun lookupClientByDni() {
        val trimmedQuery = lookupDniQuery.trim()
        if (trimmedQuery.isBlank()) {
            lookupError = "Por favor ingresa un número de DNI/C.E."
            return
        }
        viewModelScope.launch {
            val client = repository.getClientByDni(trimmedQuery)
            if (client != null) {
                trackedClient = client
                isClientRegistered = true
                lookupError = null
                
                // Greeting updated in chat
                chatMessages.add(
                    ChatMessage(
                        text = "¡Hola de nuevo, ${client.name}! He cargado los datos de tu servicio técnico. Tu **${client.vehicleType} ${client.vehicleBrand}** está en estado: **${client.status}** (${client.progress}% de avance).\n\nPregúntame lo que desees sobre la reparación o haz clic en el botón de WhatsApp.",
                        isUser = false
                    )
                )
            } else {
                lookupError = "No se encontró ningún vehículo registrado con ese DNI/C.E. ($trimmedQuery)"
            }
        }
    }

    // Save customized client updates (Admin)
    fun saveClientChanges(client: ClientEntity) {
        viewModelScope.launch {
            repository.updateClient(client)
            if (trackedClient?.id == client.id) {
                trackedClient = client
            }
            syncToGoogleSheets(client)
        }
    }

    // Delete client (Admin)
    fun deleteClient(client: ClientEntity) {
        viewModelScope.launch {
            repository.deleteClient(client)
            if (trackedClient?.id == client.id) {
                trackedClient = null
                isClientRegistered = false
            }
        }
    }

    // Add new client from Admin Dialog
    fun addNewClient(client: ClientEntity) {
        viewModelScope.launch {
            val generatedId = repository.insertClient(client)
            val clientWithId = client.copy(id = generatedId.toInt())
            syncToGoogleSheets(clientWithId)
        }
    }

    // Send chat message to Gemini
    fun sendChatMessage() {
        val text = chatInputText.trim()
        if (text.isBlank() || isChatLoading) return

        chatMessages.add(ChatMessage(text = text, isUser = true))
        chatInputText = ""
        isChatLoading = true

        viewModelScope.launch {
            // Build simple conversational context (limit to last 6 messages)
            val history = chatMessages.takeLast(7).dropLast(1).map { Pair(it.text, it.isUser) }
            val responseText = GeminiService.getChatResponse(text, history)
            chatMessages.add(ChatMessage(text = responseText, isUser = false))
            isChatLoading = false
        }
    }

    // Generate WhatsApp Business template link
    fun getWhatsAppIntentUrl(customUserQuery: String = ""): String {
        val client = trackedClient ?: return ""
        val statusText = when(client.status) {
            "Recibido" -> "📥 RECIBIDO (10% - Pendiente de diagnóstico)"
            "Diagnóstico" -> "🔍 DIAGNÓSTICO (25% - Evaluando componentes)"
            "En Reparación" -> "🔧 EN REPARACIÓN (60% - Trabajando en taller)"
            "Control de Calidad" -> "🧪 CONTROL DE CALIDAD (85% - Pruebas finales)"
            "Listo para Retirar" -> "✅ LISTO PARA ENTREGA (100% - ¡Puedes pasar a recogerlo!)"
            else -> client.status
        }
        
        val querySection = if (customUserQuery.isNotBlank()) {
            "\n❓ *Duda/Consulta del Cliente:* $customUserQuery\n"
        } else {
            ""
        }
        
        val message = """
            ⚡ *LITIO ENERGY SERVICIO TÉCNICO* ⚡
            
            ¡Hola! Solicito información de mi vehículo en servicio técnico:$querySection
            
            👤 *Cliente:* ${client.name}
            📞 *Celular:* ${client.phone}
            📧 *Email:* ${client.email}
            🏢 *Sede de Ingreso:* ${client.sede}
            
            🏍️ *Vehículo:* ${client.vehicleType}
            🏷️ *Marca/Modelo:* ${client.vehicleBrand} ${client.vehicleModel}
            🔢 *Nº Serie:* ${client.vehicleSerialNumber}
            ⚠️ *Falla Reportada:* ${client.problemDescription}
            
            📊 *Estado de Avance:* $statusText (Progreso: ${client.progress}%)
            💵 *Costo Estimado:* S/. ${String.format("%.2f", client.estimatedCost)}
            📅 *Fecha de Entrega:* ${client.estimatedCompletionDate}
            📝 *Notas del Técnico:* ${client.technicianNotes}
            
            _Enviado desde el aplicativo móvil oficial LITIO ENERGY_
        """.trimIndent()

        return try {
            val encodedMessage = URLEncoder.encode(message, "UTF-8")
            val targetPhone = when (client.sede.lowercase().trim()) {
                "litio surco" -> "975925094"
                "litio san borja" -> "904626501"
                "litio san isidro" -> "913218665"
                "litio lince" -> "987966957"
                else -> whatsappNumber.replace("+", "").replace(" ", "")
            }
            val cleanPhone = if (targetPhone.startsWith("51")) targetPhone else "51$targetPhone"
            "https://api.whatsapp.com/send?phone=$cleanPhone&text=$encodedMessage"
        } catch (e: Exception) {
            ""
        }
    }

    fun syncToGoogleSheets(client: ClientEntity) {
        var url = googleSheetsWebhookUrl.trim()
        if (url.isBlank() || !url.startsWith("http") || url.contains("docs.google.com")) {
            val targetUrl = "https://script.google.com/macros/s/AKfycbxNwzO7bX0EyZdUspdIM-9Sx-TjY2LKySZCbGvSl42rqQ5Gtq6h0gn6D6oFVMfghunaDQ/exec"
            updateGoogleSheetsWebhookUrl(targetUrl)
            url = targetUrl
        }
        viewModelScope.launch {
            isSyncingSheets = true
            syncStatusMessage = "Sincronizando..."
            val result = GoogleSheetsService.syncClientToSheets(url, client)
            isSyncingSheets = false
            result.fold(
                onSuccess = { msg ->
                    syncStatusMessage = "✅ $msg"
                },
                onFailure = { err ->
                    syncStatusMessage = "❌ Error: ${err.message}"
                }
            )
        }
    }

    fun syncAllToGoogleSheets(clients: List<ClientEntity>) {
        var url = googleSheetsWebhookUrl.trim()
        if (url.isBlank() || !url.startsWith("http") || url.contains("docs.google.com")) {
            val targetUrl = "https://script.google.com/macros/s/AKfycbxNwzO7bX0EyZdUspdIM-9Sx-TjY2LKySZCbGvSl42rqQ5Gtq6h0gn6D6oFVMfghunaDQ/exec"
            updateGoogleSheetsWebhookUrl(targetUrl)
            url = targetUrl
        }
        viewModelScope.launch {
            isSyncingSheets = true
            syncStatusMessage = "Sincronizando ${clients.size} clientes..."
            var successCount = 0
            var failureCount = 0
            var lastError = ""
            for (client in clients) {
                val result = GoogleSheetsService.syncClientToSheets(url, client)
                result.fold(
                    onSuccess = { successCount++ },
                    onFailure = { err ->
                        failureCount++
                        lastError = err.message ?: "Error"
                    }
                )
            }
            isSyncingSheets = false
            syncStatusMessage = if (failureCount == 0) {
                "✅ Sincronizados $successCount clientes con éxito."
            } else {
                "⚠️ Éxito: $successCount, Error: $failureCount. Último: $lastError"
            }
        }
    }

    fun logoutClient() {
        trackedClient = null
        isClientRegistered = false
        lookupDniQuery = ""
        lookupPhoneQuery = ""
        addSystemGreeting()
    }
}
