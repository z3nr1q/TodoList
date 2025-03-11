# TodoList - Release Notes

## Versão 1.1.0 (11/03/2025)

### Novidades (New Features)
- Sistema de categorias para tarefas
  - Categorias disponíveis: PESSOAL, TRABALHO, ESTUDOS, COMPRAS, OUTROS
  - Filtro de tarefas por categoria
  - Interface visual para seleção de categorias

### Melhorias (Improvements)
- Interface atualizada com Material Design 3
  - Nova paleta de cores para prioridades
  - Chips interativos para categorias e prioridades
  - Feedback visual aprimorado
  - Melhor organização dos elementos na tela

### Mudanças na Interface
- Adicionado seletor de categoria no diálogo de nova tarefa
- Implementado filtro de categorias na tela principal
- Melhorado visual dos cards de tarefas
- Adicionado feedback visual para lista vazia

### Mudanças Técnicas
- Atualização do modelo de dados Task
- Migração do banco de dados (versão 1 -> 2)
- Implementação de converters para novas enums
- Atualização do ViewModel para suportar categorias

### Instruções de Atualização
A atualização da versão 1.0 para 1.1 é automática:
- O banco de dados será migrado automaticamente
- Tarefas existentes receberão a categoria "OUTROS" por padrão
- Não há necessidade de ação manual do usuário

## Versão 1.0.0 (Versão Base)

### Funcionalidades Base
- CRUD básico de tarefas
- Sistema de prioridades (ALTA, MEDIA, BAIXA)
- Data de vencimento para tarefas
- Marcação de tarefas como concluídas

### Aspectos Técnicos
- Arquitetura MVVM
- Banco de dados Room
- Interface com Jetpack Compose
- Material Design 3 básico
