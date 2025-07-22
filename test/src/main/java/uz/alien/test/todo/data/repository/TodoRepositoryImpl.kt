package uz.alien.test.todo.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uz.alien.test.todo.data.local.TodoDao
import uz.alien.test.todo.data.local.toEntity
import uz.alien.test.todo.data.local.toTodo
import uz.alien.test.todo.domain.model.Todo
import uz.alien.test.todo.domain.repository.TodoRepository

class TodoRepositoryImpl(private val dao: TodoDao) : TodoRepository {

  override fun getTodos(): Flow<List<Todo>> {
    return dao.getTodos().map { list -> list.map { it.toTodo() } }
  }

  override suspend fun insertTodo(todo: Todo) {
    dao.insertTodo(todo.toEntity())
  }

  override suspend fun updateTodo(todo: Todo) {
    dao.updateTodo(todo.toEntity())
  }

  override suspend fun deleteTodo(todo: Todo) {
    dao.deleteTodo(todo.toEntity())
  }
}
