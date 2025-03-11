package com.example.todolist.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.todolist.Task
import com.example.todolist.TaskCategory
import com.example.todolist.TaskPriority
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
    var showDatePicker by remember { mutableStateOf(false) }
    var showPriorityMenu by remember { mutableStateOf(false) }
    var showCategoryMenu by remember { mutableStateOf(false) }
    var showOptionsMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                task.isCompleted -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                task.dueDate?.before(Date()) == true -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (task.isCompleted) 0.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = task.isCompleted,
                        onCheckedChange = { onToggleComplete(task) }
                    )
                    
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = if (task.isCompleted) 
                            TextDecoration.LineThrough 
                        else 
                            TextDecoration.None,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                IconButton(onClick = { showOptionsMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Opções"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Categoria
                FilterChip(
                    selected = false,
                    onClick = { showCategoryMenu = true },
                    label = {
                        Text(
                            task.category.name.lowercase()
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

                // Prioridade
                FilterChip(
                    selected = true,
                    onClick = { showPriorityMenu = true },
                    label = {
                        Text(
                            when (task.priority) {
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
                        selectedContainerColor = when (task.priority) {
                            TaskPriority.ALTA -> MaterialTheme.colorScheme.errorContainer
                            TaskPriority.MEDIA -> MaterialTheme.colorScheme.secondaryContainer
                            TaskPriority.BAIXA -> MaterialTheme.colorScheme.tertiaryContainer
                        },
                        selectedLabelColor = when (task.priority) {
                            TaskPriority.ALTA -> MaterialTheme.colorScheme.onErrorContainer
                            TaskPriority.MEDIA -> MaterialTheme.colorScheme.onSecondaryContainer
                            TaskPriority.BAIXA -> MaterialTheme.colorScheme.onTertiaryContainer
                        }
                    )
                )

                // Data
                if (task.dueDate != null) {
                    FilterChip(
                        selected = false,
                        onClick = { showDatePicker = true },
                        label = {
                            Text(
                                SimpleDateFormat(
                                    "dd/MM/yyyy",
                                    Locale.getDefault()
                                ).format(task.dueDate)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Event,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { date ->
                onUpdateDueDate(task, date)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
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
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = null,
                        tint = when (priority) {
                            TaskPriority.ALTA -> MaterialTheme.colorScheme.error
                            TaskPriority.MEDIA -> MaterialTheme.colorScheme.secondary
                            TaskPriority.BAIXA -> MaterialTheme.colorScheme.tertiary
                        }
                    )
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
                        category.name.lowercase()
                            .replaceFirstChar { it.uppercase() }
                    )
                },
                onClick = {
                    onUpdateCategory(task, category)
                    showCategoryMenu = false
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Label,
                        contentDescription = null
                    )
                }
            )
        }
    }

    // Menu de opções
    DropdownMenu(
        expanded = showOptionsMenu,
        onDismissRequest = { showOptionsMenu = false }
    ) {
        DropdownMenuItem(
            text = { Text("Excluir") },
            onClick = {
                onDelete(task)
                showOptionsMenu = false
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        )
    }
}
