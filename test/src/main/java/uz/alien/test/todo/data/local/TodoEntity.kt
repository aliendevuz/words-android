package uz.alien.test.todo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import uz.alien.test.todo.domain.model.Todo

@Entity(tableName = "todo_table")
data class TodoEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Int = 0,
  val title: String,
  val isDone: Boolean = false
)

fun TodoEntity.toTodo(): Todo {
  return Todo(
    id = id,
    title = title,
    isDone = isDone
  )
}

fun Todo.toEntity(): TodoEntity {
  return TodoEntity(
    id = id,
    title = title,
    isDone = isDone
  )
}
