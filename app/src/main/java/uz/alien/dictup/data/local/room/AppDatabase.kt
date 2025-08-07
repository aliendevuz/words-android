package uz.alien.dictup.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.alien.dictup.data.local.room.attempt.AttemptDao
import uz.alien.dictup.data.local.room.attempt.AttemptEntity
import uz.alien.dictup.data.local.room.native_story.NativeStoryDao
import uz.alien.dictup.data.local.room.native_story.NativeStoryEntity
import uz.alien.dictup.data.local.room.native_word.NativeWordDao
import uz.alien.dictup.data.local.room.native_word.NativeWordEntity
import uz.alien.dictup.data.local.room.score.ScoreDao
import uz.alien.dictup.data.local.room.score.ScoreEntity
import uz.alien.dictup.data.local.room.story.StoryDao
import uz.alien.dictup.data.local.room.story.StoryEntity
import uz.alien.dictup.data.local.room.user.UserDao
import uz.alien.dictup.data.local.room.user.UserEntity
import uz.alien.dictup.data.local.room.word.WordDao
import uz.alien.dictup.data.local.room.word.WordEntity

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