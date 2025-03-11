package com.example.todolist

import org.junit.Test
import org.junit.Assert.*
import java.util.*
import java.util.concurrent.TimeUnit

class TaskPrioritizerTest {
    
    @Test
    fun `teste prioridade ALTA para tarefa urgente de trabalho`() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1) // amanhã
        
        val priority = TaskPrioritizer.calculateSuggestedPriority(
            category = TaskCategory.TRABALHO,
            dueDate = calendar.time,
            title = "Reunião urgente com cliente"
        )
        
        assertEquals(TaskPriority.ALTA, priority)
    }
    
    @Test
    fun `teste prioridade MEDIA para tarefa de estudos sem urgência`() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 4) // daqui 4 dias
        
        val priority = TaskPrioritizer.calculateSuggestedPriority(
            category = TaskCategory.ESTUDOS,
            dueDate = calendar.time,
            title = "Estudar capítulo 3"
        )
        
        assertEquals(TaskPriority.MEDIA, priority)
    }
    
    @Test
    fun `teste prioridade BAIXA para tarefa pessoal sem data`() {
        val priority = TaskPrioritizer.calculateSuggestedPriority(
            category = TaskCategory.PESSOAL,
            dueDate = null,
            title = "Organizar fotos"
        )
        
        assertEquals(TaskPriority.BAIXA, priority)
    }
    
    @Test
    fun `teste palavras-chave de urgência aumentam prioridade`() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 7) // daqui 7 dias
        
        val priority = TaskPrioritizer.calculateSuggestedPriority(
            category = TaskCategory.COMPRAS,
            dueDate = calendar.time,
            title = "Urgente: Comprar material"
        )
        
        assertEquals(TaskPriority.MEDIA, priority) // Seria BAIXA sem a palavra "urgente"
    }
    
    @Test
    fun `teste combinação de fatores para prioridade ALTA`() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 2) // em 2 dias
        
        val priority = TaskPrioritizer.calculateSuggestedPriority(
            category = TaskCategory.TRABALHO,
            dueDate = calendar.time,
            title = "Deadline: Entregar relatório"
        )
        
        assertEquals(TaskPriority.ALTA, priority)
    }
}
