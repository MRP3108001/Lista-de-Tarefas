
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
import com.example.mylistproject.components.DialogAdicionarLembrete
import com.example.mylistproject.components.DialogEditarLembrete
import com.example.mylistproject.components.CaixaSelecaoRedonda
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
            if (lembretes.isEmpty()) {
                Text("Nenhum lembrete disponível.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(lembretes) { lembrete ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = lembrete.titulo, style = MaterialTheme.typography.titleMedium)

                                    CaixaSelecaoRedonda(
                                        checked = lembrete.concluido,
                                        onCheckedChange = {
                                            val atualizado = lembrete.copy(
                                                concluido = it,
                                                dataConclusao = if (it)
                                                    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                                                else null
                                            )

                                            scope.launch {
                                                val novaLista = lembretes.map { l ->
                                                    if (l == lembrete) atualizado else l
                                                }
                                                lembretes = novaLista
                                                usuarioLogado?.let { user ->
                                                    dataStore.salvarLembretes(user, novaLista)
                                                    lembretes = dataStore.carregarLembretes(user)
                                                }
                                            }
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = lembrete.descricao, style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Data: ${lembrete.dataHora}", style = MaterialTheme.typography.bodySmall)

                                lembrete.dataConclusao?.let { data ->
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Concluído em: $data",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                    TextButton(onClick = { lembreteSelecionado = lembrete }) {
                                        Text("Editar")
                                    }
                                    TextButton(onClick = {
                                        lembreteParaExcluir = lembrete
                                    }) {
                                        Text("Excluir")
                                    }
                                }
                            }
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
                    lembretes = lembretes.map {
                        if (it == lembrete) atualizado else it
                    }
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