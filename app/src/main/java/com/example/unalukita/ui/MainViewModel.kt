package com.example.unalukita.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.unalukita.data.local.AppDatabase
import com.example.unalukita.data.network.SheetSender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.Companion.getDatabase(app).dao()

    // Flujo de datos directo desde la DB a la UI
    val transactions = dao.getAll()

    fun syncData() {
        viewModelScope.launch(Dispatchers.IO) {
            // 1. Obtener pendientes
            val pending = dao.getUnsynced()

            // 2. Enviar uno por uno
            pending.forEach { trans ->
                val success = SheetSender.sendTransaction(trans)
                if (success) {
                    // 3. Si se envió bien, actualizar en local a "Sincronizado"
                    dao.update(trans.copy(isSynced = true))
                }
            }

            // 4. Borrar las notificaciones sincronizadas de la base de datos local
            dao.deleteSynced()
        }
    }
}