package com.example.unalukita.ui


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.unalukita.data.local.Transaction
import com.example.unalukita.ui.theme.UnaLukitaTheme
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: MainViewModel by viewModels()

        setContent {
            UnaLukitaTheme {
                MainScreen(viewModel)
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    // Observamos los datos de la base de datos como estado de Compose
    val transactionList by viewModel.transactions.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text("Control de Finanzas", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(10.dp))

        // Botón de Enviar a la Nube
        Button(
            onClick = { viewModel.syncData() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sincronizar con Google Sheet")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Lista de movimientos locales
        LazyColumn {
            items(transactionList) { item ->
                TransactionCard(item)
            }
        }
    }
}

@Composable
fun TransactionCard(t: Transaction) {
    val color = if (t.isSynced) Color(0xFFE8F5E9) else Color(0xFFFFEBEE) // Verde si enviado, Rojo si pendiente

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(text = t.banco, style = MaterialTheme.typography.titleMedium)
                Text(text = "S/ ${t.monto}", style = MaterialTheme.typography.titleMedium)
            }
            Text(text = t.descripcion, style = MaterialTheme.typography.bodySmall)

            val sdf = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
            Text(text = sdf.format(Date(t.fecha)), style = MaterialTheme.typography.labelSmall)

            Text(
                text = if(t.isSynced) "Sincronizado" else "Pendiente",
                color = if(t.isSynced) Color.Green else Color.Red,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}