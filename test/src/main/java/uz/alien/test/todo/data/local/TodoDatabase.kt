package uz.alien.test.todo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TodoEntity::class], version = 1)
abstract class TodoDatabase : RoomDatabase() {
  abstract fun todoDao(): TodoDao
}
