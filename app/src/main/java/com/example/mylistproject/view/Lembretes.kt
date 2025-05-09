package com.example.mylistproject.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mylistproject.components.*
import com.example.mylistproject.storage.DataStoreManager
import com.example.mylistproject.ui.theme.MyColors
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.MaterialTheme.typography

data class Lembrete(
    val titulo: String,
    val descricao: String,
    val dataHora: String,
    var concluido: Boolean = false,
    val dataConclusao: String? = null
)

@Composable
fun Lembretes(navController: NavController) {
    val context = LocalContext.current
    val dataStore = remember { DataStoreManager(context) }

    val todosLembretes = remember { mutableStateListOf<Lembrete>() }
    var mostrarDialogAdicionar by remember { mutableStateOf(false) }
    var indiceEdicao by remember { mutableStateOf<Int?>(null) }

    val hoje = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }

    // carregar todos os lembretes ao iniciar
    LaunchedEffect(Unit) {
        val salvos = dataStore.carregarLembretes()
        todosLembretes.clear()
        todosLembretes.addAll(salvos.map {
            Lembrete(it.titulo, it.descricao, it.dataHora, it.concluido, it.dataConclusao)
        })
    }

    // salvar automaticamente
    LaunchedEffect(todosLembretes) {
        snapshotFlow { todosLembretes.toList() }.collect { lista ->
            dataStore.salvarLembretes(lista.map {
                LembreteSerializable(it.titulo, it.descricao, it.dataHora, it.concluido, it.dataConclusao)
            })
        }
    }

    val lembretesHoje = todosLembretes.filter {
        it.dataHora.startsWith(hoje)
    }

    Scaffold(
        topBar = { Topo("Lembretes") },
        bottomBar = { BottomNavBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarDialogAdicionar = true },
                containerColor = MyColors.BotaoPrincipal
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Adicionar tarefa")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 72.dp)
            ) {
                if (lembretesHoje.isEmpty()) {
                    item {
                        Text(
                            text = "Nenhum lembrete para hoje.",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                }

                itemsIndexed(lembretesHoje) { indexVisivel, lembrete ->
                    // Localizar o índice real na lista total
                    val index = todosLembretes.indexOfFirst {
                        it.titulo == lembrete.titulo &&
                                it.descricao == lembrete.descricao &&
                                it.dataHora == lembrete.dataHora
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CaixaSelecaoRedonda(
                                    checked = lembrete.concluido,
                                    onCheckedChange = { novoValor ->
                                        val dataConclusao = if (novoValor)
                                            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                                        else null

                                        todosLembretes[index] = lembrete.copy(
                                            concluido = novoValor,
                                            dataConclusao = dataConclusao
                                        )
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = lembrete.titulo,
                                    style = typography.titleMedium,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = lembrete.descricao,
                                style = typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 40.dp),
                                textAlign = TextAlign.Justify
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Data: ${lembrete.dataHora}",
                                style = typography.bodySmall,
                                modifier = Modifier.padding(start = 40.dp),
                                color = MaterialTheme.colorScheme.primary
                            )

                            lembrete.dataConclusao?.let {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Concluído em: $it",
                                    style = typography.bodySmall,
                                    modifier = Modifier.padding(start = 40.dp),
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier
                                    .padding(start = 40.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            val novoValor = !lembrete.concluido
                                            val dataConclusao = if (novoValor)
                                                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                                            else null

                                            todosLembretes[index] = lembrete.copy(
                                                concluido = novoValor,
                                                dataConclusao = dataConclusao
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MyColors.BotaoPrincipal),
                                        modifier = Modifier.height(36.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                                    ) {
                                        Text(
                                            text = if (lembrete.concluido) "Definir como 'Não Concluído'" else "Concluir",
                                            style = typography.labelSmall
                                        )
                                    }

                                    IconButton(onClick = {
                                        indiceEdicao = index
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Editar",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    IconButton(onClick = {
                                        todosLembretes.removeAt(index)
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Excluir",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogAdicionar) {
        DialogAdicionarLembrete(
            onDismiss = { mostrarDialogAdicionar = false },
            onConfirm = { titulo, descricao, dataHora ->
                todosLembretes.add(Lembrete(titulo, descricao, dataHora))
                mostrarDialogAdicionar = false
            }
        )
    }

    indiceEdicao?.let { index ->
        val lembrete = todosLembretes[index]
        DialogEditarLembrete(
            tituloOriginal = lembrete.titulo,
            descricaoOriginal = lembrete.descricao,
            dataHoraOriginal = lembrete.dataHora,
            onDismiss = { indiceEdicao = null },
            onConfirm = { novoTitulo, novaDescricao, novaDataHora ->
                todosLembretes[index] = lembrete.copy(
                    titulo = novoTitulo,
                    descricao = novaDescricao,
                    dataHora = novaDataHora
                )
                indiceEdicao = null
            }
        )
    }
}
