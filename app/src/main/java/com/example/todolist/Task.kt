package com.example.todolist

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

enum class TaskCategory {
    PESSOAL,
    TRABALHO,
    ESTUDOS,
    COMPRAS,
    OUTROS
}

enum class TaskPriority {
    ALTA,
    MEDIA,
    BAIXA
}

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val isCompleted: Boolean = false,
    val dueDate: Date? = null,
    val priority: TaskPriority = TaskPriority.MEDIA,
    val category: TaskCategory = TaskCategory.OUTROS
)
