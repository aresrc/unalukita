package com.example.unalukita.data.network

import android.util.Log
import com.example.unalukita.data.local.Transaction
import com.example.unalukita.BuildConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object SheetSender {
    // En tu MainActivity o ViewModel
    private const val SCRIPT_URL = BuildConfig.SCRIPT_URL
    private const val SECRET_TOKEN  = BuildConfig.SECRET_TOKEN


    private val client = OkHttpClient()

    fun sendTransaction(t: Transaction): Boolean {
        return try {
            val json = JSONObject().apply {
                put("token", SECRET_TOKEN)
                put("banco", t.banco)
                put("monto", t.monto)
                put("descripcion", t.descripcion)
                put("hora", t.fecha)
            }

            val body = json.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder().url(SCRIPT_URL).post(body).build()

            client.newCall(request).execute().use { response ->
                response.isSuccessful // Retorna true si Google respondió 200 OK
            }
        } catch (e: Exception) {
            Log.e("SheetSender", "Error enviando: ${e.message}")
            false
        }
    }
}