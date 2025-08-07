package uz.alien.test.scalable.data.local.legacy

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Word::class], version = 3)
abstract class LegacyDatabase : RoomDatabase() {
    abstract fun wordDao(): LegacyDao

    companion object {
        @Volatile
        private var INSTANCE: LegacyDatabase? = null

        fun getInstance(context: Context): LegacyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LegacyDatabase::class.java,
                    "app_db"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}