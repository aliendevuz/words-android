package uz.alien.test.todo.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import uz.alien.test.todo.domain.usecase.AddTodoUseCase
import uz.alien.test.todo.domain.usecase.DeleteTodoUseCase
import uz.alien.test.todo.domain.usecase.GetTodosUseCase
import uz.alien.test.todo.domain.usecase.UpdateTodoUseCase
import uz.alien.test.todo.presenter.ToDoViewModel

class TodoViewModelFactory(
  private val getTodos: GetTodosUseCase,
  private val addToDo: AddTodoUseCase,
  private val updateToDo: UpdateTodoUseCase,
  private val deleteToDo: DeleteTodoUseCase
) : ViewModelProvider.Factory {

  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return ToDoViewModel(getTodos, addToDo, updateToDo, deleteToDo) as T
  }
}
