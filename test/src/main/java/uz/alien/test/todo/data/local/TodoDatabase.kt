package uz.alien.test.todo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TodoEntity::class], version = 1)
abstract class TodoDatabase : RoomDatabase() {
  abstract fun todoDao(): TodoDao

  companion object {
    @Volatile
    private var INSTANCE: TodoDatabase? = null

    fun getInstance(context: Context): TodoDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          TodoDatabase::class.java,
          "todo_db"
        ).build()
        INSTANCE = instance
        instance
      }
    }
  }
}
