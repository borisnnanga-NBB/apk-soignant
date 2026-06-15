package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "medicines")
data class Medicine(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val category: String, // Écorces, Infusions, Racines, Huiles, Tout
    val price: Double, // in FCFA
    val properties: String, // comma-separated, e.g. "Anti-inflammatoire, Calmant"
    val imageUrl: String,
    val isStockLow: Boolean = false
) : Serializable

@Entity(tableName = "schedule_entries")
data class ScheduleEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val day: String, // Lundi, Mardi...
    val place: String, // Mimboman, Bastos...
    val reason: String, // Consultation générale...
    val durationString: String = "2h",
    val statusString: String // "Disponible", "Occupé", "Repos"
) : Serializable

@Entity(tableName = "patient_inquiries")
data class PatientInquiry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientName: String,
    val patientPhone: String,
    val inquiryType: String, // Problème physique, Problème individuel, Problème spirituel, Maladie pathologique...
    val messageText: String,
    val audioDurationSec: Int = 0, // Mock voice recording length if recorded
    val timestamp: Long = System.currentTimeMillis(),
    val responseText: String = "",
    val scheduledMeetingDate: String = "" // if the doctor answers with a scheduled session
) : Serializable

@Entity(tableName = "orders")
data class OrderEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val medicineName: String,
    val totalPrice: Double,
    val paymentPhone: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Payé" // simulated Orange Money payment success
) : Serializable
