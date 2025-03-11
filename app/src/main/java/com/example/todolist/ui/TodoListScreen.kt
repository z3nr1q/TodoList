package com.example.todolist.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolist.TaskViewModel
import com.example.todolist.Task
import com.example.todolist.TaskPriority
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    viewModel: TaskViewModel = viewModel()
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }
    
    val tasks by viewModel.tasks.collectAsState(initial = emptyList())
    val overdueTasks by viewModel.overdueTasks.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minhas Tarefas") },
                actions = {
                    IconButton(onClick = { viewModel.toggleSortByPriority() }) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Ordenar por prioridade"
                        )
                    }
                    
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filtrar tarefas"
                        )
                    }
                }
            )
        },
        
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar tarefa"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (overdueTasks.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Tarefas Atrasadas",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        overdueTasks.forEach { task ->
                            Text(
                                text = task.title,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                items(tasks) { task ->
                    TaskItem(
                        task = task,
                        onToggleComplete = { viewModel.toggleTask(it) },
                        onDelete = { viewModel.deleteTask(it) },
                        onUpdatePriority = { task, priority -> 
                            viewModel.updateTaskPriority(task, priority)
                        },
                        onUpdateDueDate = { task, date ->
                            viewModel.updateTaskDueDate(task, date)
                        }
                    )
                }
            }
        }

        if (showAddDialog) {
            AddTaskDialog(
                onDismiss = { showAddDialog = false },
                onTaskAdded = { title, date, priority ->
                    viewModel.addTask(title, date, priority)
                    showAddDialog = false
                }
            )
        }

        DropdownMenu(
            expanded = showFilterMenu,
            onDismissRequest = { showFilterMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("Todas") },
                onClick = {
                    viewModel.showAllTasks()
                    showFilterMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text("Pendentes") },
                onClick = {
                    viewModel.showIncompleteTasks()
                    showFilterMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text("Concluídas") },
                onClick = {
                    viewModel.showCompletedTasks()
                    showFilterMenu = false
                }
            )
        }
    }
}
