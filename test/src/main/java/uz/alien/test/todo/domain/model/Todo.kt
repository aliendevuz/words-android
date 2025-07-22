package uz.alien.test.todo.domain.model

data class Todo(
  val id: Int = 0,
  val title: String,
  val isDone: Boolean = false
)
