package com.example.mylistproject.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
            lembretes = dataStore.carregarLembretes(it)
        }
    }

    val lembretesDoDiaSelecionado = lembretes.filter {
        it.dataHora.take(10).trim() == dataSelecionada.trim()
    }

    val lembretesPendentes = lembretesDoDiaSelecionado.filter { !it.concluido }
    val lembretesConcluidos = lembretesDoDiaSelecionado.filter { it.concluido }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = { Topo("Calendário") },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
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
                    if (lembretesPendentes.isNotEmpty()) {
                        Text("Pendentes", style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        lembretesPendentes.forEach { lembrete ->
                            CardLembrete(
                                lembrete = lembrete,
                                onConcluir = { concluido ->
                                    val atualizado = lembrete.copy(
                                        concluido = concluido,
                                        dataConclusao = if (concluido)
                                            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                                        else null
                                    )
                                    scope.launch {
                                        lembretes = lembretes.map { if (it == lembrete) atualizado else it }
                                        usuarioLogado?.let { user ->
                                            dataStore.salvarLembretes(user, lembretes)
                                        }
                                    }
                                },
                                onEditar = { lembreteParaEditar = it },
                                onExcluir = { lembreteParaExcluir = it }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    if (lembretesConcluidos.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Concluídos", style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        lembretesConcluidos.forEach { lembrete ->
                            CardLembrete(
                                lembrete = lembrete,
                                onConcluir = { concluido ->
                                    val atualizado = lembrete.copy(
                                        concluido = concluido,
                                        dataConclusao = if (concluido)
                                            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                                        else null
                                    )
                                    scope.launch {
                                        lembretes = lembretes.map { if (it == lembrete) atualizado else it }
                                        usuarioLogado?.let { user ->
                                            dataStore.salvarLembretes(user, lembretes)
                                        }
                                    }
                                },
                                onEditar = { lembreteParaEditar = it },
                                onExcluir = { lembreteParaExcluir = it }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
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
