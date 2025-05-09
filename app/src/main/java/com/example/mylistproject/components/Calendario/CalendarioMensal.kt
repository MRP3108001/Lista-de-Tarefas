package com.example.mylistproject.components.calendario

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mylistproject.view.Lembrete
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarioMensal(
    lembretes: List<Lembrete>,
    onDateSelected: (String) -> Unit
) {
    val calendar = remember { Calendar.getInstance() }
    var mesAtual by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var anoAtual by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }

    // Calcula quantos dias no mês atual
    val diasNoMes = remember(mesAtual, anoAtual) {
        calendar.set(Calendar.MONTH, mesAtual)
        calendar.set(Calendar.YEAR, anoAtual)
        calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    val diasSemana = listOf("D", "S", "T", "Q", "Q", "S", "S")

    Column {
        // Topo com mês e navegação
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                if (mesAtual == 0) {
                    mesAtual = 11
                    anoAtual -= 1
                } else {
                    mesAtual -= 1
                }
            }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Mês anterior")
            }

            val nomeMes = SimpleDateFormat("MMMM yyyy", Locale("pt", "BR")).format(
                GregorianCalendar(anoAtual, mesAtual, 1).time
            ).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }

            Text(text = nomeMes, style = MaterialTheme.typography.titleMedium)

            IconButton(onClick = {
                if (mesAtual == 11) {
                    mesAtual = 0
                    anoAtual += 1
                } else {
                    mesAtual += 1
                }
            }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Próximo mês")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Cabeçalho dos dias da semana
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            diasSemana.forEach { dia ->
                Text(
                    text = dia,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Corpo do calendário
        val primeiroDiaDoMes = GregorianCalendar(anoAtual, mesAtual, 1).get(Calendar.DAY_OF_WEEK)
        val totalDias = diasNoMes + (primeiroDiaDoMes - 1)
        val semanas = (totalDias / 7) + 1

        Column {
            var dia = 1
            for (semana in 1..semanas) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (diaSemana in 1..7) {
                        if ((semana == 1 && diaSemana < primeiroDiaDoMes) || dia > diasNoMes) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            )
                        } else {
                            val dataFormatada = String.format("%02d/%02d/%04d", dia, mesAtual + 1, anoAtual)
                            val possuiLembrete = lembretes.any {
                                it.dataHora.startsWith(dataFormatada)
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(4.dp)
                                    .clickable { onDateSelected(dataFormatada) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (possuiLembrete) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "$dia")
                                    }
                                } else {
                                    Text(text = "$dia")
                                }
                            }
                            dia++
                        }
                    }
                }
            }
        }
    }
}
