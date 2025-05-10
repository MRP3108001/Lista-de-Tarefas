package com.example.mylistproject.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mylistproject.view.LembreteSerializable

@Composable
fun DialogEditarLembrete(
    lembrete: LembreteSerializable,
    onDismiss: () -> Unit,
    onConfirm: (LembreteSerializable) -> Unit
) {
    var titulo by remember { mutableStateOf(lembrete.titulo) }
    var descricao by remember { mutableStateOf(lembrete.descricao) }
    var dataHora by remember { mutableStateOf(lembrete.dataHora) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (titulo.isNotBlank() && dataHora.isNotBlank()) {
                        onConfirm(
                            lembrete.copy(
                                titulo = titulo,
                                descricao = descricao,
                                dataHora = dataHora
                            )
                        )
                    }
                }
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = { Text("Editar Lembrete") },
        text = {
            Column {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                DateTimePicker { novaDataHora ->
                    dataHora = novaDataHora
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Data atual: $dataHora", style = MaterialTheme.typography.bodySmall)
            }
        }
    )
}
