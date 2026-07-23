package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleChatDao {
    @Query("SELECT * FROM vehicle_chats WHERE clientId = :clientId ORDER BY timestamp ASC")
    fun getMessagesForClientFlow(clientId: Int): Flow<List<VehicleChatMessageEntity>>

    @Query("SELECT * FROM vehicle_chats WHERE clientId = :clientId ORDER BY timestamp ASC")
    suspend fun getMessagesForClient(clientId: Int): List<VehicleChatMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: VehicleChatMessageEntity): Long

    @Query("DELETE FROM vehicle_chats WHERE clientId = :clientId")
    suspend fun deleteMessagesForClient(clientId: Int)
}
