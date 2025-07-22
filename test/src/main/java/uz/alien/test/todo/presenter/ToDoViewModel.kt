package uz.alien.test.todo.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.alien.test.todo.domain.model.Todo
import uz.alien.test.todo.domain.usecase.AddTodoUseCase
import uz.alien.test.todo.domain.usecase.DeleteTodoUseCase
import uz.alien.test.todo.domain.usecase.GetTodosUseCase
import uz.alien.test.todo.domain.usecase.UpdateTodoUseCase

class ToDoViewModel(
  private val getTodos: GetTodosUseCase,
  private val addToDo: AddTodoUseCase,
  private val updateToDo: UpdateTodoUseCase,
  private val deleteToDo: DeleteTodoUseCase
) : ViewModel() {

  private val _todos = MutableStateFlow<List<Todo>>(emptyList())
  val todos: StateFlow<List<Todo>> = _todos.asStateFlow()

  init {
    viewModelScope.launch {
      getTodos().collect { list ->
        _todos.value = list
      }
    }
  }

  fun add(todo: Todo) = viewModelScope.launch {
    addToDo(todo)
  }

  fun update(todo: Todo) = viewModelScope.launch {
    updateToDo(todo)
  }

  fun delete(todo: Todo) = viewModelScope.launch {
    deleteToDo(todo)
  }
}
