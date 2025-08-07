package uz.alien.dictup.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.alien.dictup.domain.repository.DataStoreRepository
import uz.alien.dictup.domain.repository.retrofit.RemoteNativeStoryRepository
import uz.alien.dictup.domain.repository.retrofit.RemoteNativeWordRepository
import uz.alien.dictup.domain.repository.retrofit.RemoteStoryRepository
import uz.alien.dictup.domain.repository.retrofit.RemoteWordRepository
import uz.alien.dictup.domain.repository.room.NativeStoryRepository
import uz.alien.dictup.domain.repository.room.NativeWordRepository
import uz.alien.dictup.domain.repository.room.ScoreRepository
import uz.alien.dictup.domain.repository.room.StoryRepository
import uz.alien.dictup.domain.repository.room.UserRepository
import uz.alien.dictup.domain.repository.room.WordRepository
import uz.alien.dictup.domain.usecase.main.GetAllNativeStoryUseCase
import uz.alien.dictup.domain.usecase.main.GetAllNativeWordUseCase
import uz.alien.dictup.domain.usecase.main.GetAllScoreUseCase
import uz.alien.dictup.domain.usecase.main.GetAllStoryUseCase
import uz.alien.dictup.domain.usecase.main.GetAllWordsUseCase
import uz.alien.dictup.domain.usecase.main.MainUseCases
import uz.alien.dictup.domain.usecase.on_internet_connected.OnInternetConnectedUseCases
import uz.alien.dictup.domain.usecase.on_internet_connected.SyncDataUseCaseAndSetupScore
import uz.alien.dictup.domain.usecase.startup.CreateUserUseCase
import uz.alien.dictup.domain.usecase.startup.StartupUseCases

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideMainUseCases(
        wordRepository: WordRepository,
        storyRepository: StoryRepository,
        nativeWordRepository: NativeWordRepository,
        nativeStoryRepository: NativeStoryRepository,
        scoreRepository: ScoreRepository
    ): MainUseCases {
        return MainUseCases(
            GetAllWordsUseCase(wordRepository),
            GetAllStoryUseCase(storyRepository),
            GetAllNativeWordUseCase(nativeWordRepository),
            GetAllNativeStoryUseCase(nativeStoryRepository),
            GetAllScoreUseCase(scoreRepository)
        )
    }

    @Provides
    fun provideOnInternetConnectedUseCases(
        @ApplicationContext context: Context,
        remoteWordRepository: RemoteWordRepository,
        remoteStoryRepository: RemoteStoryRepository,
        remoteNativeWordRepository: RemoteNativeWordRepository,
        remoteNativeStoryRepository: RemoteNativeStoryRepository,
        dataStoreRepository: DataStoreRepository,
        wordRepository: WordRepository,
        storyRepository: StoryRepository,
        nativeWordRepository: NativeWordRepository,
        nativeStoryRepository: NativeStoryRepository,
        scoreRepository: ScoreRepository
    ): OnInternetConnectedUseCases {
        return OnInternetConnectedUseCases(
            SyncDataUseCaseAndSetupScore(
                context = context,
                remoteWordRepository = remoteWordRepository,
                remoteStoryRepository = remoteStoryRepository,
                remoteNativeWordRepository = remoteNativeWordRepository,
                remoteNativeStoryRepository = remoteNativeStoryRepository,
                dataStoreRepository = dataStoreRepository,
                wordRepository = wordRepository,
                storyRepository = storyRepository,
                nativeWordRepository = nativeWordRepository,
                nativeStoryRepository = nativeStoryRepository,
                scoreRepository = scoreRepository
            )
        )
    }

    @Provides
    fun provideStartupUseCases(
        userRepository: UserRepository,
        dataStoreRepository: DataStoreRepository
    ): StartupUseCases {
        return StartupUseCases(
            CreateUserUseCase(
                userRepository,
                dataStoreRepository
            )
        )
    }
}