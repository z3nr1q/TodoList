package com.example.todolist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val database = TaskDatabase.getDatabase(application)
    private val taskDao = database.taskDao()

    // Estado para controlar a ordenação e filtragem
    private val _filterCompleted = MutableStateFlow<Boolean?>(null)
    private val _sortByPriority = MutableStateFlow(false)
    private val _filterCategory = MutableStateFlow<TaskCategory?>(null)

    // Combina os filtros com as tarefas
    val tasks: StateFlow<List<Task>> = combine(
        taskDao.getAllTasks(),
        _filterCompleted,
        _sortByPriority,
        _filterCategory
    ) { tasks, filterCompleted, sortByPriority, filterCategory ->
        var result = tasks
        
        // Aplica filtro de status (completo/incompleto)
        filterCompleted?.let { completed ->
            result = result.filter { it.isCompleted == completed }
        }

        // Aplica filtro de categoria
        filterCategory?.let { category ->
            result = result.filter { it.category == category }
        }

        // Aplica ordenação por prioridade
        if (sortByPriority) {
            result = result.sortedBy { 
                when (it.priority) {
                    TaskPriority.ALTA -> 0
                    TaskPriority.MEDIA -> 1
                    TaskPriority.BAIXA -> 2
                }
            }
        }

        result
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Tarefas atrasadas
    val overdueTasks = taskDao.getOverdueTasks(System.currentTimeMillis())
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun addTask(
        title: String, 
        dueDate: Date? = null, 
        priority: TaskPriority = TaskPriority.MEDIA,
        category: TaskCategory = TaskCategory.OUTROS
    ) {
        viewModelScope.launch {
            taskDao.insertTask(
                Task(
                    title = title,
                    dueDate = dueDate,
                    priority = priority,
                    category = category
                )
            )
        }
    }

    fun toggleTask(task: Task) {
        viewModelScope.launch {
            taskDao.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskDao.deleteTask(task)
        }
    }

    fun updateTaskPriority(task: Task, priority: TaskPriority) {
        viewModelScope.launch {
            taskDao.updateTask(task.copy(priority = priority))
        }
    }

    fun updateTaskDueDate(task: Task, dueDate: Date?) {
        viewModelScope.launch {
            taskDao.updateTask(task.copy(dueDate = dueDate))
        }
    }

    fun updateTaskCategory(task: Task, category: TaskCategory) {
        viewModelScope.launch {
            taskDao.updateTask(task.copy(category = category))
        }
    }

    // Funções para controlar filtros e ordenação
    fun showAllTasks() {
        _filterCompleted.value = null
        _filterCategory.value = null
    }

    fun showCompletedTasks() {
        _filterCompleted.value = true
    }

    fun showIncompleteTasks() {
        _filterCompleted.value = false
    }

    fun filterByCategory(category: TaskCategory?) {
        _filterCategory.value = category
    }

    fun toggleSortByPriority() {
        _sortByPriority.value = !_sortByPriority.value
    }
}
