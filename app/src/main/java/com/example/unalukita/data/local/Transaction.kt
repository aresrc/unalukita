package com.example.unalukita.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val banco: String,       // "Yape", "Interbank", "Lemon"
    val monto: Double,
    val descripcion: String,
    val fecha: Long,         // Timestamp
    val isSynced: Boolean = false // Para saber si ya se envió a Google
)
