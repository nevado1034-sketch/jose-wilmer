package com.example.data

import kotlinx.coroutines.flow.Flow

class ClientRepository(private val clientDao: ClientDao) {
    val allClients: Flow<List<ClientEntity>> = clientDao.getAllClients()

    suspend fun getClientById(id: Int): ClientEntity? {
        return clientDao.getClientById(id)
    }

    suspend fun getClientByPhone(phone: String): ClientEntity? {
        return clientDao.getClientByPhone(phone)
    }

    suspend fun getClientByDni(dni: String): ClientEntity? {
        return clientDao.getClientByDni(dni)
    }

    suspend fun insertClient(client: ClientEntity): Long {
        return clientDao.insertClient(client)
    }

    suspend fun updateClient(client: ClientEntity) {
        clientDao.updateClient(client)
    }

    suspend fun deleteClient(client: ClientEntity) {
        clientDao.deleteClient(client)
    }

    suspend fun deleteClientById(id: Int) {
        clientDao.deleteClientById(id)
    }
}
