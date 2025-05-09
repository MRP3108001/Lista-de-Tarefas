package com.example.mylistproject.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mylistproject.components.BottomNavBar
import com.example.mylistproject.components.Topo
import com.example.mylistproject.components.calendario.CalendarioMensal
import com.example.mylistproject.components.calendario.CardLembrete
import com.example.mylistproject.storage.DataStoreManager
import com.example.mylistproject.ui.theme.MyColors
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun Calendario(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = remember { DataStoreManager(context) }

    var lembretes by remember { mutableStateOf<List<Lembrete>>(emptyList()) }
    var dataSelecionada by remember { mutableStateOf("") }

    // carregar lembretes do dia atual em diante
    LaunchedEffect(Unit) {
        val salvos = dataStore.carregarLembretes()
        val hoje = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        lembretes = salvos.map {
            Lembrete(
                it.titulo,
                it.descricao,
                it.dataHora,
                it.concluido,
                it.dataConclusao
            )
        }.filter {
            try {
                val data = it.dataHora.split(" ")[0]
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = sdf.parse(data)
                val today = sdf.parse(hoje)
                date != null && today != null && !date.before(today)
            } catch (e: Exception) {
                false
            }
        }
    }

    val lembretesDoDiaSelecionado = lembretes.filter {
        it.dataHora.startsWith(dataSelecionada)
    }

    Scaffold(
        topBar = { Topo("Calendário") },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            CalendarioMensal(
                lembretes = lembretes,
                onDateSelected = { data ->
                    dataSelecionada = data
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (dataSelecionada.isNotEmpty()) "Lembretes em $dataSelecionada:" else "Selecione uma data",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (dataSelecionada.isNotEmpty()) {
                if (lembretesDoDiaSelecionado.isEmpty()) {
                    Text("Nenhum lembrete para esta data.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(lembretesDoDiaSelecionado) { lembrete ->
                            CardLembrete(lembrete)
                        }
                    }
                }
            }
        }
    }
}
