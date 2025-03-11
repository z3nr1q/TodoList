package com.example.todolist.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    // Criamos um calendário para manipular as datas
    val calendar = Calendar.getInstance()
    
    // Estado do DatePicker com a data atual como padrão
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = calendar.timeInMillis
    )

    // Diálogo do Material Design 3
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                // Quando uma data é selecionada, convertemos para Date
                datePickerState.selectedDateMillis?.let { millis ->
                    calendar.timeInMillis = millis
                    onDateSelected(calendar.time)
                }
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    ) {
        // O DatePicker em si, configurado para mostrar apenas o calendário
        DatePicker(
            state = datePickerState,
            showModeToggle = false // Não mostra o botão de alternar entre calendário/input
        )
    }
}
