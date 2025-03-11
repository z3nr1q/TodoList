package com.example.todolist

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY CASE WHEN dueDate IS NULL THEN 1 ELSE 0 END, dueDate ASC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = :completed")
    fun getTasksByStatus(completed: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM tasks ORDER BY " +
           "CASE priority " +
           "WHEN 'ALTA' THEN 1 " +
           "WHEN 'MEDIA' THEN 2 " +
           "WHEN 'BAIXA' THEN 3 END")
    fun getTasksByPriority(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE dueDate IS NOT NULL AND dueDate < :currentTime AND isCompleted = 0")
    fun getOverdueTasks(currentTime: Long): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)
}
