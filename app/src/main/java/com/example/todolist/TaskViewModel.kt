package com.example.todolist

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val taskDao = TaskDatabase.getDatabase(application).taskDao()
    private val _selectedCategory = MutableStateFlow<TaskCategory?>(null)
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks = combine(
        _tasks,
        _selectedCategory
    ) { tasks, category ->
        when (category) {
            null -> tasks
            else -> tasks.filter { it.category == category }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val selectedCategory: TaskCategory?
        get() = _selectedCategory.value

    var selectedCategoryState by mutableStateOf<TaskCategory?>(null)
        private set

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            taskDao.getAllTasks().collect { taskList ->
                _tasks.value = taskList
            }
        }
    }

    fun addTask(
        title: String,
        dueDate: Date? = null,
        category: TaskCategory = TaskCategory.OUTROS,
        useSmartPriority: Boolean = true
    ) {
        viewModelScope.launch {
            val priority = if (useSmartPriority) {
                TaskPrioritizer.calculateSuggestedPriority(category, dueDate, title)
            } else {
                TaskPriority.MEDIA
            }
            
            val task = Task(
                title = title,
                dueDate = dueDate,
                priority = priority,
                category = category
            )
            taskDao.insert(task)
        }
    }

    fun recalculateTaskPriority(task: Task) {
        viewModelScope.launch {
            val newPriority = TaskPrioritizer.calculateSuggestedPriority(
                task.category,
                task.dueDate,
                task.title
            )
            if (newPriority != task.priority) {
                taskDao.update(task.copy(priority = newPriority))
            }
        }
    }

    fun recalculateAllPriorities() {
        viewModelScope.launch {
            _tasks.value.forEach { task ->
                recalculateTaskPriority(task)
            }
        }
    }

    fun toggleTaskCompleted(task: Task) {
        viewModelScope.launch {
            taskDao.update(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskDao.delete(task)
        }
    }

    fun updateTaskPriority(task: Task, priority: TaskPriority) {
        viewModelScope.launch {
            taskDao.update(task.copy(priority = priority))
        }
    }

    fun updateTaskDueDate(task: Task, dueDate: Date?) {
        viewModelScope.launch {
            val updatedTask = task.copy(dueDate = dueDate)
            // Recalcula a prioridade quando a data é atualizada
            val newPriority = TaskPrioritizer.calculateSuggestedPriority(
                updatedTask.category,
                updatedTask.dueDate,
                updatedTask.title
            )
            taskDao.update(updatedTask.copy(priority = newPriority))
        }
    }

    fun updateTaskCategory(task: Task, category: TaskCategory) {
        viewModelScope.launch {
            val updatedTask = task.copy(category = category)
            // Recalcula a prioridade quando a categoria é atualizada
            val newPriority = TaskPrioritizer.calculateSuggestedPriority(
                updatedTask.category,
                updatedTask.dueDate,
                updatedTask.title
            )
            taskDao.update(updatedTask.copy(priority = newPriority))
        }
    }

    fun filterByCategory(category: TaskCategory?) {
        _selectedCategory.value = category
        selectedCategoryState = category
    }
}
