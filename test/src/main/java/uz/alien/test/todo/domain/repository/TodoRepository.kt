package uz.alien.test.todo.domain.repository

import kotlinx.coroutines.flow.Flow
import uz.alien.test.todo.domain.model.Todo

interface TodoRepository {
  fun getTodos(): Flow<List<Todo>>
  suspend fun insertTodo(todo: Todo)
  suspend fun updateTodo(todo: Todo)
  suspend fun deleteTodo(todo: Todo)
}
