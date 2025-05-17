package com.example.mylistproject.components.calendario

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mylistproject.components.CaixaSelecaoRedonda
import com.example.mylistproject.view.LembreteSerializable


@Composable
fun CardLembrete(
    lembrete: LembreteSerializable,
    onEditar: (LembreteSerializable) -> Unit,
    onExcluir: (LembreteSerializable) -> Unit,
    onConcluir: (Boolean) -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = lembrete.titulo,
                    style = MaterialTheme.typography.titleMedium
                )
                CaixaSelecaoRedonda(
                    checked = lembrete.concluido,
                    onCheckedChange = { onConcluir(it) }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = lembrete.descricao,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Justify
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Data agendada: ${lembrete.dataHora}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )

            lembrete.dataConclusao?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Concluído em: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { onEditar(lembrete) }) {
                    Text("Editar")
                }
                TextButton(onClick = { onExcluir(lembrete) }) {
                    Text("Excluir")
                }
            }
        }
    }
}
