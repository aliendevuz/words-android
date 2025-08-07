package uz.alien.dictup.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.alien.dictup.data.repository.room.AttemptRepositoryImpl
import uz.alien.dictup.data.repository.room.NativeStoryRepositoryImpl
import uz.alien.dictup.data.repository.room.NativeWordRepositoryImpl
import uz.alien.dictup.data.repository.room.ScoreRepositoryImpl
import uz.alien.dictup.data.repository.room.StoryRepositoryImpl
import uz.alien.dictup.data.repository.room.UserRepositoryImpl
import uz.alien.dictup.data.repository.room.WordRepositoryImpl
import uz.alien.dictup.domain.repository.room.AttemptRepository
import uz.alien.dictup.domain.repository.room.NativeStoryRepository
import uz.alien.dictup.domain.repository.room.NativeWordRepository
import uz.alien.dictup.domain.repository.room.ScoreRepository
import uz.alien.dictup.domain.repository.room.StoryRepository
import uz.alien.dictup.domain.repository.room.UserRepository
import uz.alien.dictup.domain.repository.room.WordRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    @Binds
    abstract fun bindWordRepository(
        impl: WordRepositoryImpl
    ): WordRepository

    @Binds
    abstract fun bindStoryRepository(
        impl: StoryRepositoryImpl
    ): StoryRepository

    @Binds
    abstract fun bindNativeWordRepository(
        impl: NativeWordRepositoryImpl
    ): NativeWordRepository

    @Binds
    abstract fun bindNativeStoryRepository(
        impl: NativeStoryRepositoryImpl
    ): NativeStoryRepository

    @Binds
    abstract fun bindScoreRepository(
        impl: ScoreRepositoryImpl
    ): ScoreRepository

    @Binds
    abstract fun bindAttemptRepository(
        impl: AttemptRepositoryImpl
    ): AttemptRepository
}