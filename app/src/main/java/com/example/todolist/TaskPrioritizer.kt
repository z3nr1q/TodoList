package com.example.todolist

import java.util.*
import kotlin.math.max
import kotlin.math.min

class TaskPrioritizer {
    companion object {
        private const val URGENCY_THRESHOLD_HIGH = 2 // dias
        private const val URGENCY_THRESHOLD_MEDIUM = 5 // dias
        
        fun calculateSuggestedPriority(
            category: TaskCategory,
            dueDate: Date?,
            title: String
        ): TaskPriority {
            var score = 0
            
            // Fator 1: Categoria
            score += when (category) {
                TaskCategory.TRABALHO -> 3
                TaskCategory.ESTUDOS -> 2
                TaskCategory.PESSOAL -> 1
                TaskCategory.COMPRAS -> 1
                TaskCategory.OUTROS -> 0
            }
            
            // Fator 2: Urgência baseada na data
            if (dueDate != null) {
                val daysUntilDue = calculateDaysUntilDue(dueDate)
                score += when {
                    daysUntilDue <= URGENCY_THRESHOLD_HIGH -> 3
                    daysUntilDue <= URGENCY_THRESHOLD_MEDIUM -> 2
                    else -> 0
                }
            }
            
            // Fator 3: Palavras-chave de urgência no título
            val urgentKeywords = listOf("urgente", "importante", "prazo", "deadline", "hoje", "amanhã")
            if (urgentKeywords.any { title.lowercase().contains(it) }) {
                score += 2
            }
            
            // Normaliza o score e retorna a prioridade
            return when {
                score >= 4 -> TaskPriority.ALTA
                score >= 2 -> TaskPriority.MEDIA
                else -> TaskPriority.BAIXA
            }
        }
        
        private fun calculateDaysUntilDue(dueDate: Date): Int {
            val today = Calendar.getInstance()
            val due = Calendar.getInstance().apply { time = dueDate }
            
            // Normaliza as datas para comparar apenas dias
            today.set(Calendar.HOUR_OF_DAY, 0)
            today.set(Calendar.MINUTE, 0)
            today.set(Calendar.SECOND, 0)
            today.set(Calendar.MILLISECOND, 0)
            
            due.set(Calendar.HOUR_OF_DAY, 0)
            due.set(Calendar.MINUTE, 0)
            due.set(Calendar.SECOND, 0)
            due.set(Calendar.MILLISECOND, 0)
            
            val diffInMillis = due.timeInMillis - today.timeInMillis
            return max(0, (diffInMillis / (1000 * 60 * 60 * 24)).toInt())
        }
    }
}
