package com.example.mylistproject.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DateTimePicker(
    onDateTimeSelected: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedDateTime by remember { mutableStateOf("") }

    Button(onClick = {
        showDateTimePicker(context) {
            selectedDateTime = it
            onDateTimeSelected(it)
        }
    }) {
        Text(text = if (selectedDateTime.isEmpty()) "Selecionar Data e Hora" else selectedDateTime)
    }
}

fun showDateTimePicker(context: Context, onSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()

    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    calendar.set(year, month, dayOfMonth, hourOfDay, minute)
                    val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val formattedDateTime = format.format(calendar.time)
                    onSelected(formattedDateTime)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}
