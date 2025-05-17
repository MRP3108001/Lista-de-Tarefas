package com.example.mylistproject.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mylistproject.components.*
import com.example.mylistproject.components.calendario.CardLembrete
import com.example.mylistproject.storage.DataStoreManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun Lembretes(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = remember { DataStoreManager(context) }

    var usuarioLogado by remember { mutableStateOf<String?>(null) }
    var lembretes by remember { mutableStateOf<List<LembreteSerializable>>(emptyList()) }
    var exibirDialogoAdicionar by remember { mutableStateOf(false) }
    var lembreteSelecionado by remember { mutableStateOf<LembreteSerializable?>(null) }
    var lembreteParaExcluir by remember { mutableStateOf<LembreteSerializable?>(null) }

    LaunchedEffect(Unit) {
        usuarioLogado = dataStore.obterUsuarioLogado()
        usuarioLogado?.let {
            lembretes = dataStore.carregarLembretes(it)
        }
    }

    // Filtro: apenas lembretes com data de hoje
    val dataHoje = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    val lembretesDoDia = lembretes.filter {
        it.dataHora.startsWith(dataHoje)
    }

    val lembretesPendentes = lembretesDoDia.filter { !it.concluido }
    val lembretesConcluidos = lembretesDoDia.filter { it.concluido }

    Scaffold(
        topBar = { Topo("Lembretes") },
        bottomBar = { BottomNavBar(navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = { exibirDialogoAdicionar = true }) {
                Text("+")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (lembretesDoDia.isEmpty()) {
                Text("Nenhum lembrete para hoje.")
            } else {
                if (lembretesPendentes.isNotEmpty()) {
                    Text("Pendentes", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(lembretesPendentes) { lembrete ->
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
                                        val novaLista = lembretes.map { if (it == lembrete) atualizado else it }
                                        lembretes = novaLista
                                        usuarioLogado?.let { user ->
                                            dataStore.salvarLembretes(user, novaLista)
                                            lembretes = dataStore.carregarLembretes(user)
                                        }
                                    }
                                },
                                onEditar = { lembreteSelecionado = it },
                                onExcluir = { lembreteParaExcluir = it }
                            )
                        }
                    }
                }

                if (lembretesConcluidos.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Concluídos", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(lembretesConcluidos) { lembrete ->
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
                                        val novaLista = lembretes.map { if (it == lembrete) atualizado else it }
                                        lembretes = novaLista
                                        usuarioLogado?.let { user ->
                                            dataStore.salvarLembretes(user, novaLista)
                                            lembretes = dataStore.carregarLembretes(user)
                                        }
                                    }
                                },
                                onEditar = { lembreteSelecionado = it },
                                onExcluir = { lembreteParaExcluir = it }
                            )
                        }
                    }
                }
            }
        }
    }

    if (exibirDialogoAdicionar) {
        DialogAdicionarLembrete(
            onDismiss = { exibirDialogoAdicionar = false },
            onConfirm = { titulo, descricao, dataHora ->
                val novo = LembreteSerializable(
                    titulo = titulo,
                    descricao = descricao,
                    dataHora = dataHora
                )
                scope.launch {
                    lembretes = lembretes + novo
                    usuarioLogado?.let { user ->
                        dataStore.salvarLembretes(user, lembretes)
                        lembretes = dataStore.carregarLembretes(user)
                    }
                    exibirDialogoAdicionar = false
                }
            }
        )
    }

    lembreteSelecionado?.let { lembrete ->
        DialogEditarLembrete(
            lembrete = lembrete,
            onDismiss = { lembreteSelecionado = null },
            onConfirm = { atualizado ->
                scope.launch {
                    lembretes = lembretes.map { if (it == lembrete) atualizado else it }
                    usuarioLogado?.let { user ->
                        dataStore.salvarLembretes(user, lembretes)
                        lembretes = dataStore.carregarLembretes(user)
                    }
                    lembreteSelecionado = null
                }
            }
        )
    }

    lembreteParaExcluir?.let { lembrete ->
        AlertDialog(
            onDismissRequest = { lembreteParaExcluir = null },
            title = { Text("Excluir Lembrete") },
            text = { Text("Tem certeza que deseja excluir este lembrete?") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        lembretes = lembretes.filter { it != lembrete }
                        usuarioLogado?.let { user ->
                            dataStore.salvarLembretes(user, lembretes)
                            lembretes = dataStore.carregarLembretes(user)
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
