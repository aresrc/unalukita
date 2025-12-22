package com.example.unalukita.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.unalukita.data.local.AppDatabase
import com.example.unalukita.data.local.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class NotificationReader : NotificationListenerService() {

    private val db by lazy { AppDatabase.getDatabase(applicationContext) }
    private val scope = CoroutineScope(Dispatchers.IO)

    companion object{
        private val TARGET_PACKAGES = setOf(
            "pe.com.interbank.mobilebanking",
            "com.bcp.innovacxion.yapeapp",
            "com.applemoncash",
            "com.samsung.knox.securefolder" // <--- EL INTERMEDIARIO
        )
    }
    // Agregamos el paquete de Samsung Secure Folder


    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val packageName = sbn?.packageName ?: return

        // Filtro rápido
        if (packageName !in TARGET_PACKAGES) return

        val extras = sbn.notification.extras
        val title = extras.getString(Notification.EXTRA_TITLE) ?: ""
        val text = extras.getString(Notification.EXTRA_TEXT) ?: ""

        // Unimos todo para buscar palabras clave
        val fullContent = "$title $text"
        Log.d("FINANZAS_DEBUG", "App: $packageName | Content: $fullContent")

        val isTransfer = fullContent.contains("te envió") ||
                fullContent.contains("recibiste", true) ||
                fullContent.contains("pago recibido")

        if (isTransfer) {
            when (packageName) {
                "com.bcp.innovacxion.yapeapp" -> processYape(fullContent)
                "pe.com.interbank.mobilebanking" -> processInterbank(fullContent)
                "com.applemoncash" -> processLemon(fullContent)
            }
        }

    }

    private fun processYape(text: String) {
        val matcher = Pattern.compile("S/\\s*(\\d+(\\.\\d{1,2})?)").matcher(text)
        if (matcher.find()) {
            val monto = matcher.group(1)?.toDoubleOrNull() ?: 0.0
            saveToDb("Yape (Secure)", monto, text)
        }
    }

    private fun processInterbank(text: String) {
        val matcher = Pattern.compile("(S/|USD)\\s*(\\d+(\\.\\d{1,2})?)").matcher(text)
        if (matcher.find()) {
            val monto = matcher.group(2)?.toDoubleOrNull() ?: 0.0
            saveToDb("Interbank (Secure)", monto, text)
        }
    }

    private fun processLemon(text: String) {
        val matcher = Pattern.compile("(S/|USD)\\s*(\\d+(\\.\\d{1,2})?)").matcher(text)

        if (matcher.find()) {
            val monto = matcher.group(2)?.toDoubleOrNull() ?: 0.0
            saveToDb("Interbank (Secure)", monto, text)
        }
    }

    private fun saveToDb(banco: String, monto: Double, desc: String) {
        scope.launch {
            db.dao().insert(
                Transaction(
                    banco = banco,
                    monto = monto,
                    descripcion = desc,
                    fecha = System.currentTimeMillis(),
                    isSynced = false
                )
            )
        }
    }
}