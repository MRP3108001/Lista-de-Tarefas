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
import com.example.mylistproject.components.DialogEditarLembrete
import com.example.mylistproject.storage.DataStoreManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun Calendario(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = remember { DataStoreManager(context) }

    var usuarioLogado by remember { mutableStateOf<String?>(null) }
    var lembretes by remember { mutableStateOf<List<LembreteSerializable>>(emptyList()) }
    var dataSelecionada by remember { mutableStateOf("") }
    var lembreteParaEditar by remember { mutableStateOf<LembreteSerializable?>(null) }
    var lembreteParaExcluir by remember { mutableStateOf<LembreteSerializable?>(null) }

    LaunchedEffect(Unit) {
        usuarioLogado = dataStore.obterUsuarioLogado()
        usuarioLogado?.let {
            val salvos = dataStore.carregarLembretes(it)
            val hoje = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            lembretes = salvos.filter { lembrete ->
                try {
                    val data = lembrete.dataHora.split(" ")[0]
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val date = sdf.parse(data)
                    val today = sdf.parse(hoje)
                    date != null && today != null && !date.before(today)
                } catch (e: Exception) {
                    false
                }
            }
        }
    }

    val lembretesDoDiaSelecionado = lembretes.filter {
        it.dataHora.take(10).trim() == dataSelecionada.trim()
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
                onDateSelected = { dataSelecionada = it }
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
                            CardLembrete(
                                lembrete = lembrete,
                                onEditar = { lembreteParaEditar = it },
                                onExcluir = { lembreteParaExcluir = it }
                            )
                        }
                    }
                }
            }
        }

        lembreteParaEditar?.let { lembrete ->
            DialogEditarLembrete(
                lembrete = lembrete,
                onDismiss = { lembreteParaEditar = null },
                onConfirm = { atualizado ->
                    scope.launch {
                        lembretes = lembretes.map {
                            if (it == lembrete) atualizado else it
                        }
                        usuarioLogado?.let { user ->
                            dataStore.salvarLembretes(user, lembretes)
                        }
                        lembreteParaEditar = null
                    }
                }
            )
        }

        lembreteParaExcluir?.let { lembrete ->
            AlertDialog(
                onDismissRequest = { lembreteParaExcluir = null },
                title = { Text("Excluir Lembrete") },
                text = { Text("Deseja realmente excluir este lembrete?") },
                confirmButton = {
                    TextButton(onClick = {
                        scope.launch {
                            lembretes = lembretes.filter { it != lembrete }
                            usuarioLogado?.let { user ->
                                dataStore.salvarLembretes(user, lembretes)
                            }
                            lembreteParaExcluir = null
                        }
                    }) {
                        Text("Excluir")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { lembreteParaExcluir = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
