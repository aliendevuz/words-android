package uz.alien.test.todo.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.alien.test.todo.domain.model.Todo
import uz.alien.test.todo.domain.repository.TodoRepository

class GetTodosUseCase(private val repository: TodoRepository) {
  operator fun invoke(): Flow<List<Todo>> = repository.getTodos()
}
