package uz.alien.test.todo.domain.usecase

import uz.alien.test.todo.domain.model.Todo
import uz.alien.test.todo.domain.repository.TodoRepository

class UpdateTodoUseCase(private val repo: TodoRepository) {
  suspend operator fun invoke(todo: Todo) = repo.updateTodo(todo)
}
