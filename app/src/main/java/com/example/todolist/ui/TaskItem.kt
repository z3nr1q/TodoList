package com.example.todolist.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.todolist.Task
import com.example.todolist.TaskCategory
import com.example.todolist.TaskPriority
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
    val haptic = LocalHapticFeedback.current
    
    val isOverdue = task.dueDate?.before(Date()) == true && !task.isCompleted
    val cardColor = when {
        task.isCompleted -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        isOverdue -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surface
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .animateContentSize()
            .combinedClickable(
                onClick = {},
                onLongClick = { 
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    showOptionsMenu = true 
                }
            ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = cardColor
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (task.isCompleted) 0.dp else 2.dp,
            pressedElevation = if (task.isCompleted) 0.dp else 4.dp
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = task.isCompleted,
                        onCheckedChange = { 
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onToggleComplete(task) 
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = when {
                                isOverdue -> MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.primary
                            }
                        )
                    )
                    
                    Column {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.titleMedium,
                            textDecoration = if (task.isCompleted) 
                                TextDecoration.LineThrough 
                            else 
                                TextDecoration.None,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.alpha(if (task.isCompleted) 0.7f else 1f)
                        )
                        
                        if (isOverdue) {
                            Text(
                                text = "Atrasada",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { showOptionsMenu = true }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = "Opções"
                    )
                }
            }

            AnimatedVisibility(
                visible = !task.isCompleted,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Categoria
                    SuggestionChip(
                        onClick = { showCategoryMenu = true },
                        label = {
                            Text(
                                task.category.name.lowercase()
                                    .replaceFirstChar { it.uppercase() }
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Label,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
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
                                imageVector = when (task.priority) {
                                    TaskPriority.ALTA -> Icons.Filled.PriorityHigh
                                    TaskPriority.MEDIA -> Icons.Filled.FiberManualRecord
                                    TaskPriority.BAIXA -> Icons.Filled.ArrowDownward
                                },
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
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
                        AssistChip(
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
                                    imageVector = Icons.Outlined.Event,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                    }
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
                leadingIcon = {
                    Icon(
                        imageVector = when (priority) {
                            TaskPriority.ALTA -> Icons.Filled.PriorityHigh
                            TaskPriority.MEDIA -> Icons.Filled.FiberManualRecord
                            TaskPriority.BAIXA -> Icons.Filled.ArrowDownward
                        },
                        contentDescription = null
                    )
                },
                onClick = {
                    onUpdatePriority(task, priority)
                    showPriorityMenu = false
                }
            )
        }
    }

    // Menu de categoria
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
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Label,
                        contentDescription = null
                    )
                },
                onClick = {
                    onUpdateCategory(task, category)
                    showCategoryMenu = false
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
            leadingIcon = {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            onClick = {
                onDelete(task)
                showOptionsMenu = false
            },
            colors = MenuDefaults.itemColors(
                textColor = MaterialTheme.colorScheme.error
            )
        )
    }
}
