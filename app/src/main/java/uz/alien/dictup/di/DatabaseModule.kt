package uz.alien.dictup.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import uz.alien.dictup.data.local.room.AppDatabase
import uz.alien.dictup.data.local.room.attempt.AttemptDao
import uz.alien.dictup.data.local.room.native_story.NativeStoryDao
import uz.alien.dictup.data.local.room.native_word.NativeWordDao
import uz.alien.dictup.data.local.room.score.ScoreDao
import uz.alien.dictup.data.local.room.story.StoryDao
import uz.alien.dictup.data.local.room.user.UserDao
import uz.alien.dictup.data.local.room.word.WordDao

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    @Provides
    fun provideWordDao(appDatabase: AppDatabase): WordDao {
        return appDatabase.wordDao()
    }

    @Provides
    fun provideStoryDao(appDatabase: AppDatabase): StoryDao {
        return appDatabase.storyDao()
    }

    @Provides
    fun provideNativeWordDao(appDatabase: AppDatabase): NativeWordDao {
        return appDatabase.nativeWordDao()
    }

    @Provides
    fun provideNativeStoryDao(appDatabase: AppDatabase): NativeStoryDao {
        return appDatabase.nativeStoryDao()
    }

    @Provides
    fun provideScoreDao(appDatabase: AppDatabase): ScoreDao {
        return appDatabase.scoreDao()
    }

    @Provides
    fun provideAttemptDao(appDatabase: AppDatabase): AttemptDao {
        return appDatabase.attemptDao()
    }
}