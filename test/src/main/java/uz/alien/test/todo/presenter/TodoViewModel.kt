package uz.alien.test.todo.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.alien.test.todo.domain.model.Todo
import uz.alien.test.todo.domain.usecase.AddTodoUseCase
import uz.alien.test.todo.domain.usecase.DeleteTodoUseCase
import uz.alien.test.todo.domain.usecase.GetTodosUseCase
import uz.alien.test.todo.domain.usecase.UpdateTodoUseCase
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
  private val addTodoUseCase: AddTodoUseCase,
  private val getTodosUseCase: GetTodosUseCase,
  private val updateTodoUseCase: UpdateTodoUseCase,
  private val deleteTodoUseCase: DeleteTodoUseCase
) : ViewModel() {

  private val _todos = MutableStateFlow<List<Todo>>(emptyList())
  val todos: StateFlow<List<Todo>> = _todos.asStateFlow()

  init {
    viewModelScope.launch {
      getTodosUseCase().collect { list ->
        _todos.value = list
      }
    }
  }

  fun add(todo: Todo) = viewModelScope.launch {
    addTodoUseCase(todo)
  }

  fun update(todo: Todo) = viewModelScope.launch {
    updateTodoUseCase(todo)
  }

  fun delete(todo: Todo) = viewModelScope.launch {
    deleteTodoUseCase(todo)
  }
}
