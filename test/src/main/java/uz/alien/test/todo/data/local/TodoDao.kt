package uz.alien.test.todo.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

  @Query("SELECT * FROM todo_table")
  fun getTodos(): Flow<List<TodoEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTodo(todo: TodoEntity)

  @Update
  suspend fun updateTodo(todo: TodoEntity)

  @Delete
  suspend fun deleteTodo(todo: TodoEntity)
}
