package com.example.todolist.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todolist.TaskPriority
import com.example.todolist.TaskCategory
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onTaskAdded: (String, Date?, TaskPriority, TaskCategory) -> Unit
) {
    // Estados para armazenar os dados da tarefa
    var taskTitle by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    var selectedPriority by remember { mutableStateOf(TaskPriority.MEDIA) }
    var selectedCategory by remember { mutableStateOf(TaskCategory.OUTROS) }
    var showDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nova Tarefa") },
        text = {
            // Conteúdo do diálogo
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                // Campo de título
                OutlinedTextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = { Text("Título da Tarefa") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botão de data
                Button(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (selectedDate != null)
                            "Data: ${selectedDate?.let { 
                                java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it) 
                            }}"
                        else
                            "Definir Data de Vencimento"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Seleção de categoria
                Text("Categoria:", style = MaterialTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TaskCategory.values().forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { 
                                Text(category.name.lowercase()
                                    .replaceFirstChar { it.uppercase() })
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Seleção de prioridade
                Text("Prioridade:", style = MaterialTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Chips de prioridade
                    TaskPriority.values().forEach { priority ->
                        FilterChip(
                            selected = selectedPriority == priority,
                            onClick = { selectedPriority = priority },
                            label = { 
                                Text(
                                    when (priority) {
                                        TaskPriority.ALTA -> "Alta"
                                        TaskPriority.MEDIA -> "Média"
                                        TaskPriority.BAIXA -> "Baixa"
                                    }
                                ) 
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (priority) {
                                    TaskPriority.ALTA -> MaterialTheme.colorScheme.errorContainer
                                    TaskPriority.MEDIA -> MaterialTheme.colorScheme.secondaryContainer
                                    TaskPriority.BAIXA -> MaterialTheme.colorScheme.tertiaryContainer
                                }
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (taskTitle.isNotBlank()) {
                        onTaskAdded(taskTitle, selectedDate, selectedPriority, selectedCategory)
                        onDismiss()
                    }
                }
            ) {
                Text("Adicionar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )

    // Mostra o seletor de data quando necessário
    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { date ->
                selectedDate = date
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}
