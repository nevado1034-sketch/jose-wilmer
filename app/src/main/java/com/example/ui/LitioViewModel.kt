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
    
    // Status tracking query state
    var lookupPhoneQuery by mutableStateOf("")
    var lookupError by mutableStateOf<String?>(null)
    
    private val prefs = application.getSharedPreferences("litio_settings", android.content.Context.MODE_PRIVATE)

    // WhatsApp configuration
    var whatsappNumber by mutableStateOf(prefs.getString("whatsapp_number", "+51975925094") ?: "+51975925094")

    // Google Sheets Webhook URL
    var googleSheetsWebhookUrl by mutableStateOf(prefs.getString("google_sheets_url", "")?.ifEmpty { "https://script.google.com/macros/s/AKfycbzWYdRUsL6evWEocpaGAKr1NGWeDvABPK5K2jNZsZ16ZmIGV-D-njhitSLf6n0AAfaj-A/exec" } ?: "https://script.google.com/macros/s/AKfycbzWYdRUsL6evWEocpaGAKr1NGWeDvABPK5K2jNZsZ16ZmIGV-D-njhitSLf6n0AAfaj-A/exec")

    var isSyncingSheets by mutableStateOf(false)
    var syncStatusMessage by mutableStateOf<String?>(null)

    fun updateWhatsappNumber(number: String) {
        whatsappNumber = number
        prefs.edit().putString("whatsapp_number", number).apply()
    }

    fun updateGoogleSheetsWebhookUrl(url: String) {
        googleSheetsWebhookUrl = url
        prefs.edit().putString("google_sheets_url", url).apply()
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
                text = "¡Hola! Bienvenido a **LITIO ENERGY SERVICIO TECNICO** ⚡\n\nSoy **LitioBot**, tu experto en micromovilidad eléctrica. ¿Tienes alguna pregunta sobre tu batería, presión de neumáticos o pastillas de freno? Cualquier problema solo consúltame para tu ayuda.",
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
                vehicleSerialNumber = regVehicleSerial.ifBlank { "S/N-PENDIENTE" },
                problemDescription = regProblem.ifBlank { "Revisión general preventiva" },
                status = "Recibido",
                progress = 10,
                technicianNotes = "Vehículo registrado en taller. Pendiente de ingreso a bahía de diagnóstico.",
                estimatedCost = 20.00,
                estimatedCompletionDate = "Pendiente de diagnóstico"
            )
            repository.insertClient(newClient)
            trackedClient = newClient
            isClientRegistered = true
            
            syncToGoogleSheets(newClient)
            
            // Auto add note in chat
            chatMessages.add(
                ChatMessage(
                    text = "¡Excelente, ${newClient.name}! He registrado tu **${newClient.vehicleType} ${newClient.vehicleBrand} ${newClient.vehicleModel}** con éxito en nuestro sistema de Litio Energy. Su estado actual es **${newClient.status}** (10%).\n\n¿Quieres saber qué revisaremos en el diagnóstico inicial o necesitas consultar algo más?",
                    isUser = false
                )
            )
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
            repository.insertClient(client)
            syncToGoogleSheets(client)
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
            val cleanPhone = whatsappNumber.replace("+", "").replace(" ", "")
            "https://api.whatsapp.com/send?phone=$cleanPhone&text=$encodedMessage"
        } catch (e: Exception) {
            ""
        }
    }

    fun syncToGoogleSheets(client: ClientEntity) {
        val url = googleSheetsWebhookUrl
        if (url.isBlank()) {
            syncStatusMessage = "URL de Sheets no configurada. Edítala en el panel de administración."
            return
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

    fun logoutClient() {
        trackedClient = null
        isClientRegistered = false
        addSystemGreeting()
    }
}
