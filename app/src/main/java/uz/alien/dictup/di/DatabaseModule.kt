package uz.alien.dictup.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import uz.alien.dictup.data.local.room.AppDatabase
import uz.alien.dictup.data.local.room.dao.NativeStoryDao
import uz.alien.dictup.data.local.room.dao.NativeWordDao
import uz.alien.dictup.data.local.room.dao.ScoreDao
import uz.alien.dictup.data.local.room.dao.StoryDao
import uz.alien.dictup.data.local.room.dao.WordDao
import uz.alien.dictup.data.repository.room.NativeStoryRepositoryImpl
import uz.alien.dictup.data.repository.room.NativeWordRepositoryImpl
import uz.alien.dictup.data.repository.room.ScoreRepositoryImpl
import uz.alien.dictup.data.repository.room.StoryRepositoryImpl
import uz.alien.dictup.data.repository.room.WordRepositoryImpl
import uz.alien.dictup.domain.repository.room.NativeStoryRepository
import uz.alien.dictup.domain.repository.room.NativeWordRepository
import uz.alien.dictup.domain.repository.room.ScoreRepository
import uz.alien.dictup.domain.repository.room.StoryRepository
import uz.alien.dictup.domain.repository.room.WordRepository

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
        )
            .fallbackToDestructiveMigration(false)
            .build()
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
    fun provideWordRepository(wordDao: WordDao): WordRepository {
        return WordRepositoryImpl(wordDao)
    }

    @Provides
    fun provideStoryRepository(storyDao: StoryDao): StoryRepository {
        return StoryRepositoryImpl(storyDao)
    }

    @Provides
    fun provideNativeWordRepository(nativeWordDao: NativeWordDao): NativeWordRepository {
        return NativeWordRepositoryImpl(nativeWordDao)
    }

    @Provides
    fun provideNativeStoryRepository(nativeStoryDao: NativeStoryDao): NativeStoryRepository {
        return NativeStoryRepositoryImpl(nativeStoryDao)
    }

    @Provides
    fun provideScoreRepository(scoreDao: ScoreDao): ScoreRepository {
        return ScoreRepositoryImpl(scoreDao)
    }
}