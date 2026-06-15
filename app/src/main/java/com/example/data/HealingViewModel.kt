package com.example.data

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.URLEncoder

class HealingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HealingRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = HealingRepository(database)
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    // Exposing reactive database streams directly to Compose UI with stateIn
    val medicines: StateFlow<List<Medicine>> = repository.allMedicines
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val scheduleEntries: StateFlow<List<ScheduleEntry>> = repository.allScheduleEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inquiries: StateFlow<List<PatientInquiry>> = repository.allInquiries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<OrderEntry>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    // State Machine parameters for screen transitions
    private val _currentScreen = MutableStateFlow("auth") // screen hierarchy: "auth", "patient_home", "service_detail", "shop", "soignant", "admin"
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    private val _currentUserRole = MutableStateFlow("visiteur") // "visiteur", "patient", "soignant", "admin"
    val currentUserRole: StateFlow<String> = _currentUserRole.asStateFlow()

    private val _currentPatientName = MutableStateFlow("Mamadou")
    val currentPatientName: StateFlow<String> = _currentPatientName.asStateFlow()

    private val _currentPatientPhone = MutableStateFlow("+237699112233")
    val currentPatientPhone: StateFlow<String> = _currentPatientPhone.asStateFlow()

    // Service interaction states
    private val _selectedServiceType = MutableStateFlow("Problème physique")
    val selectedServiceType: StateFlow<String> = _selectedServiceType.asStateFlow()

    // Dialog & payment triggers
    private val _orangePaymentMedicine = MutableStateFlow<Medicine?>(null)
    val orangePaymentMedicine: StateFlow<Medicine?> = _orangePaymentMedicine.asStateFlow()

    // Helper functions to change screens
    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    fun setPatientName(name: String) {
        _currentPatientName.value = name
    }

    fun setPatientPhone(phone: String) {
        _currentPatientPhone.value = phone
    }

    fun selectService(serviceType: String) {
        _selectedServiceType.value = serviceType
        navigateTo("service_detail")
    }

    fun showPaymentModal(medicine: Medicine?) {
        _orangePaymentMedicine.value = medicine
    }

    // Authentication simulation
    fun loginAs(role: String, name: String = "Mamadou", phone: String = "+237 699112233") {
        _currentUserRole.value = role
        if (role == "patient") {
            _currentPatientName.value = name
            _currentPatientPhone.value = phone
            navigateTo("patient_home")
        } else if (role == "soignant") {
            navigateTo("soignant")
        } else if (role == "admin") {
            navigateTo("admin")
        } else {
            navigateTo("auth")
        }
    }

    // DB Operations called from Admin or User Interactions
    fun addMedicine(medicine: Medicine) {
        viewModelScope.launch {
            repository.insertMedicine(medicine)
        }
    }

    fun deleteMedicine(id: Int) {
        viewModelScope.launch {
            repository.deleteMedicineById(id)
        }
    }

    fun addScheduleEntry(entry: ScheduleEntry) {
        viewModelScope.launch {
            repository.insertScheduleEntry(entry)
        }
    }

    fun deleteSchedule(id: Int) {
        viewModelScope.launch {
            repository.deleteScheduleEntryById(id)
        }
    }

    fun saveAdminResponse(inquiryId: Int, responseText: String, meetingDate: String = "") {
        viewModelScope.launch {
            val inq = inquiries.value.find { it.id == inquiryId }
            if (inq != null) {
                val updated = inq.copy(
                    responseText = responseText, 
                    scheduledMeetingDate = meetingDate
                )
                repository.updateInquiry(updated)

                // If doctor schedules a meeting, add it automatically to Papa George's schedule!
                if (meetingDate.isNotEmpty()) {
                    val entry = ScheduleEntry(
                        day = meetingDate,
                        place = "Clinique Mimbomane",
                        reason = "Consultation Spécifique avec " + inq.patientName,
                        durationString = "1h30",
                        statusString = "Occupé"
                    )
                    repository.insertScheduleEntry(entry)
                }
            }
        }
    }

    // WhatsApp intent and local save handler for Service Consultation Description
    fun sendWhatsAppInquiry(
        context: Context,
        inquiryType: String,
        description: String,
        bodyPartsSelected: List<String> = emptyList()
    ) {
        val patientName = _currentPatientName.value
        val patientPhone = _currentPatientPhone.value

        // Custom details builder
        val bodyPartsText = if (bodyPartsSelected.isNotEmpty()) {
            "\nZones douloureuses: ${bodyPartsSelected.joinToString(", ")}"
        } else ""

        val rawMessage = """
            Bonjour Papa GEORGE,
            Je m'appelle $patientName ($patientPhone).
            Je souhaiterais une consultation pour le service: $inquiryType
            Description de mon mal: $description $bodyPartsText
        """.trimIndent()

        // 1. Save local inquiry so Healer can see it inside the app dashboard!
        viewModelScope.launch {
            repository.insertInquiry(
                PatientInquiry(
                    patientName = patientName,
                    patientPhone = patientPhone,
                    inquiryType = inquiryType,
                    messageText = "$description $bodyPartsText"
                )
            )
        }

        // 2. Open WhatsApp link to Healer's number
        openWhatsAppMessage(context, "+237695413620", rawMessage)
    }

    // Distance Consultation direct link (predefined form)
    fun contactDistanceConsultation(context: Context) {
        val patientName = _currentPatientName.value
        val rawMessage = "Monsieur $patientName voudrait avoir une consultation à distance"

        // Local save
        viewModelScope.launch {
            repository.insertInquiry(
                PatientInquiry(
                    patientName = patientName,
                    patientPhone = _currentPatientPhone.value,
                    inquiryType = "Consultation à distance",
                    messageText = "Demande directe de consultation à distance sur WhatsApp"
                )
            )
        }

        openWhatsAppMessage(context, "+237695413620", rawMessage)
    }

    // Orange Money Payment Simulated Execution
    fun simulateOrangeMoneyPayment(context: Context, product: Medicine, number: String) {
        viewModelScope.launch {
            _orangePaymentMedicine.value = null
            repository.insertOrder(
                OrderEntry(
                    medicineName = product.name,
                    totalPrice = product.price,
                    paymentPhone = number
                )
            )
            Toast.makeText(
                context, 
                "Orange Money: Demande de débit initiée sur le $number.\nCFA ${product.price} seront transférés à Papa GEORGE.", 
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun openWhatsAppMessage(context: Context, phone: String, message: String) {
        try {
            val url = "https://api.whatsapp.com/send?phone=${phone.replace("+", "")}&text=${URLEncoder.encode(message, "UTF-8")}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp n'est pas installé sur cet appareil.", Toast.LENGTH_SHORT).show()
        }
    }
}
