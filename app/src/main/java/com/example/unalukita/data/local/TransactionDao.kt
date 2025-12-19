package com.example.unalukita.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: Transaction)

    // Obtenemos todas las transacciones ordenadas por fecha (Flow actualiza la UI auto)
    @Query("SELECT * FROM transactions ORDER BY fecha DESC")
    fun getAll(): Flow<List<Transaction>>

    // Obtener solo las pendientes de envío
    @Query("SELECT * FROM transactions WHERE isSynced = 0")
    suspend fun getUnsynced(): List<Transaction>

    @Update
    suspend fun update(transaction: Transaction)

    @Query("DELETE FROM transactions")
    suspend fun clearAll()

    @Query("DELETE FROM transactions WHERE isSynced = 1")
    suspend fun deleteSynced()
}