package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val dni: String = "",
    val email: String,
    val vehicleType: String, // "Scooter", "Bicicleta", "Moto"
    val vehicleBrand: String,
    val vehicleModel: String,
    val vehicleSerialNumber: String,
    val problemDescription: String,
    val status: String, // "Recibido", "Diagnóstico", "En Reparación", "Control de Calidad", "Listo para Retirar"
    val progress: Int, // 0 to 100
    val technicianNotes: String,
    val estimatedCost: Double,
    val estimatedCompletionDate: String,
    val sede: String = "Litio Surco",
    val createdAt: Long = System.currentTimeMillis()
)
