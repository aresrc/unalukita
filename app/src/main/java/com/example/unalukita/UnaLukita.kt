package com.example.unalukita

import android.app.Application
import com.example.unalukita.data.local.AppDatabase

class UnaLukita : Application() {
    @Suppress("unused")
    val database by lazy { AppDatabase.getDatabase(this) }
}