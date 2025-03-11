package com.example.todolist.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todolist.TaskCategory
import com.example.todolist.TaskPriority
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onTaskAdded: (String, Date?, TaskPriority, TaskCategory) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    var selectedPriority by remember { mutableStateOf(TaskPriority.MEDIA) }
    var selectedCategory by remember { mutableStateOf(TaskCategory.OUTROS) }
    var showDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nova Tarefa") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Campo de título
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Seleção de categoria
                Text(
                    text = "Categoria",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskCategory.values().forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { 
                                Text(
                                    category.name.lowercase()
                                        .replaceFirstChar { it.uppercase() }
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Label,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }

                // Seleção de prioridade
                Text(
                    text = "Prioridade",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Flag,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (priority) {
                                    TaskPriority.ALTA -> MaterialTheme.colorScheme.errorContainer
                                    TaskPriority.MEDIA -> MaterialTheme.colorScheme.secondaryContainer
                                    TaskPriority.BAIXA -> MaterialTheme.colorScheme.tertiaryContainer
                                },
                                selectedLabelColor = when (priority) {
                                    TaskPriority.ALTA -> MaterialTheme.colorScheme.onErrorContainer
                                    TaskPriority.MEDIA -> MaterialTheme.colorScheme.onSecondaryContainer
                                    TaskPriority.BAIXA -> MaterialTheme.colorScheme.onTertiaryContainer
                                }
                            )
                        )
                    }
                }

                // Seletor de data
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        selectedDate?.let { 
                            java.text.SimpleDateFormat(
                                "dd/MM/yyyy", 
                                Locale.getDefault()
                            ).format(it) 
                        } ?: "Definir data de vencimento"
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onTaskAdded(title, selectedDate, selectedPriority, selectedCategory)
                        onDismiss()
                    }
                },
                enabled = title.isNotBlank()
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
