package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicle_chats")
data class VehicleChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clientId: Int,
    val senderRole: String, // "CLIENT" or "TECHNICIAN"
    val senderName: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)
