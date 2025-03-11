package com.example.todolist.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.todolist.Task
import com.example.todolist.TaskCategory
import com.example.todolist.TaskPriority
import com.example.todolist.TaskViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    viewModel: TaskViewModel
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val tasks by viewModel.tasks.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Lista de Tarefas",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar tarefa")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filtros de categoria
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 1.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Categorias",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                    )
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = viewModel.selectedCategory == null,
                                onClick = { viewModel.filterByCategory(null) },
                                label = { Text("Todas") },
                                leadingIcon = if (viewModel.selectedCategory == null) {
                                    {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                } else null
                            )
                        }
                        
                        items(TaskCategory.values()) { category ->
                            FilterChip(
                                selected = viewModel.selectedCategory == category,
                                onClick = { viewModel.filterByCategory(category) },
                                label = { 
                                    Text(
                                        category.name.lowercase()
                                            .replaceFirstChar { it.uppercase() }
                                    )
                                },
                                leadingIcon = if (viewModel.selectedCategory == category) {
                                    {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                } else {
                                    {
                                        Icon(
                                            Icons.Default.Label,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Lista de tarefas
            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (viewModel.selectedCategory == null)
                                "Nenhuma tarefa encontrada"
                            else
                                "Nenhuma tarefa na categoria ${
                                    viewModel.selectedCategory.toString()
                                        .lowercase()
                                        .replaceFirstChar { it.uppercase() }
                                }",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(tasks) { task ->
                        TaskItem(
                            task = task,
                            onToggleComplete = viewModel::toggleTaskCompleted,
                            onDelete = viewModel::deleteTask,
                            onUpdatePriority = viewModel::updateTaskPriority,
                            onUpdateDueDate = viewModel::updateTaskDueDate,
                            onUpdateCategory = viewModel::updateTaskCategory
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onTaskAdded = { title, dueDate, priority, category ->
                viewModel.addTask(title, dueDate, priority, category)
                showAddDialog = false
            }
        )
    }
}
