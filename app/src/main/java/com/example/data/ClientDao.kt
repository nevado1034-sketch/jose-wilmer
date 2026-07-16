package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    @Query("SELECT * FROM clients ORDER BY createdAt DESC")
    fun getAllClients(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM clients WHERE id = :id LIMIT 1")
    suspend fun getClientById(id: Int): ClientEntity?

    @Query("SELECT * FROM clients WHERE phone = :phone LIMIT 1")
    suspend fun getClientByPhone(phone: String): ClientEntity?

    @Query("SELECT * FROM clients WHERE LOWER(dni) = LOWER(:dni) LIMIT 1")
    suspend fun getClientByDni(dni: String): ClientEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: ClientEntity): Long

    @Update
    suspend fun updateClient(client: ClientEntity)

    @Delete
    suspend fun deleteClient(client: ClientEntity)

    @Query("DELETE FROM clients WHERE id = :id")
    suspend fun deleteClientById(id: Int)
}
