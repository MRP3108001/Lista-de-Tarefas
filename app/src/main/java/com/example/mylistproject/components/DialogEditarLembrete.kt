package com.example.mylistproject.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DialogEditarLembrete(
    tituloOriginal: String,
    descricaoOriginal: String,
    dataHoraOriginal: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var titulo by remember { mutableStateOf(tituloOriginal) }
    var descricao by remember { mutableStateOf(descricaoOriginal) }
    var dataHora by remember { mutableStateOf(dataHoraOriginal) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (titulo.isNotBlank() && dataHora.isNotBlank()) {
                        onConfirm(titulo, descricao, dataHora)
                        // campos não precisam ser limpos, pois o dialog será fechado
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
