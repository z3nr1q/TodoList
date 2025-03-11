package com.example.todolist.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.todolist.Task
import com.example.todolist.TaskPriority
import com.example.todolist.TaskCategory
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskItem(
    task: Task,
    onToggleComplete: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    onUpdatePriority: (Task, TaskPriority) -> Unit,
    onUpdateDueDate: (Task, Date?) -> Unit,
    onUpdateCategory: (Task, TaskCategory) -> Unit
) {
    var showPriorityMenu by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showCategoryMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Checkbox e título
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Checkbox(
                        checked = task.isCompleted,
                        onCheckedChange = { onToggleComplete(task) }
                    )
                    Column(
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.bodyLarge,
                            textDecoration = if (task.isCompleted) 
                                TextDecoration.LineThrough 
                            else 
                                TextDecoration.None
                        )
                        
                        // Categoria e Data em uma linha
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            // Categoria
                            AssistChip(
                                onClick = { showCategoryMenu = true },
                                label = { 
                                    Text(
                                        task.category.name.lowercase()
                                            .replaceFirstChar { it.uppercase() }
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Category,
                                        contentDescription = "Categoria",
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            
                            // Data de vencimento
                            task.dueDate?.let { date ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Data de vencimento",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = SimpleDateFormat(
                                            "dd/MM/yyyy", 
                                            Locale.getDefault()
                                        ).format(date),
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(start = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Ícones de ações
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botão de prioridade
                    IconButton(onClick = { showPriorityMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = "Prioridade",
                            tint = when (task.priority) {
                                TaskPriority.ALTA -> MaterialTheme.colorScheme.error
                                TaskPriority.MEDIA -> MaterialTheme.colorScheme.secondary
                                TaskPriority.BAIXA -> MaterialTheme.colorScheme.tertiary
                            }
                        )
                    }

                    // Botão de excluir
                    IconButton(onClick = { onDelete(task) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Excluir tarefa"
                        )
                    }
                }
            }
        }
    }

    // Menu de prioridade
    DropdownMenu(
        expanded = showPriorityMenu,
        onDismissRequest = { showPriorityMenu = false }
    ) {
        TaskPriority.values().forEach { priority ->
            DropdownMenuItem(
                text = { 
                    Text(
                        when (priority) {
                            TaskPriority.ALTA -> "Alta"
                            TaskPriority.MEDIA -> "Média"
                            TaskPriority.BAIXA -> "Baixa"
                        }
                    )
                },
                onClick = {
                    onUpdatePriority(task, priority)
                    showPriorityMenu = false
                }
            )
        }
    }

    // Menu de categorias
    DropdownMenu(
        expanded = showCategoryMenu,
        onDismissRequest = { showCategoryMenu = false }
    ) {
        TaskCategory.values().forEach { category ->
            DropdownMenuItem(
                text = { 
                    Text(
                        category.name.lowercase().replaceFirstChar { it.uppercase() }
                    )
                },
                onClick = {
                    onUpdateCategory(task, category)
                    showCategoryMenu = false
                }
            )
        }
    }

    // Seletor de data
    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { date ->
                onUpdateDueDate(task, date)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}
