package uz.alien.test.todo.domain.usecase

import uz.alien.test.todo.domain.model.Todo
import uz.alien.test.todo.domain.repository.TodoRepository

class DeleteTodoUseCase(private val repo: TodoRepository) {
  suspend operator fun invoke(todo: Todo) = repo.deleteTodo(todo)
}
