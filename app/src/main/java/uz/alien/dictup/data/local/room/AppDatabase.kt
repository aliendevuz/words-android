package uz.alien.dictup.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.alien.dictup.data.local.room.dao.AttemptDao
import uz.alien.dictup.data.local.room.entity.AttemptEntity
import uz.alien.dictup.data.local.room.dao.NativeStoryDao
import uz.alien.dictup.data.local.room.entity.NativeStoryEntity
import uz.alien.dictup.data.local.room.dao.NativeWordDao
import uz.alien.dictup.data.local.room.entity.NativeWordEntity
import uz.alien.dictup.data.local.room.dao.ScoreDao
import uz.alien.dictup.data.local.room.entity.ScoreEntity
import uz.alien.dictup.data.local.room.dao.StoryDao
import uz.alien.dictup.data.local.room.entity.StoryEntity
import uz.alien.dictup.data.local.room.dao.UserDao
import uz.alien.dictup.data.local.room.entity.UserEntity
import uz.alien.dictup.data.local.room.dao.WordDao
import uz.alien.dictup.data.local.room.entity.WordEntity

@Database(
    entities = [
        UserEntity::class,
        WordEntity::class,
        StoryEntity::class,
        NativeWordEntity::class,
        NativeStoryEntity::class,
        ScoreEntity::class,
        AttemptEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun wordDao(): WordDao

    abstract fun storyDao(): StoryDao

    abstract fun nativeWordDao(): NativeWordDao

    abstract fun nativeStoryDao(): NativeStoryDao

    abstract fun scoreDao(): ScoreDao

    abstract fun attemptDao(): AttemptDao
}