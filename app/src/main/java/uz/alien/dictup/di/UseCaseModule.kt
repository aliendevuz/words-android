package uz.alien.dictup.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.alien.dictup.domain.repository.DataStoreRepository
import uz.alien.dictup.domain.repository.RemoteConfigRepository
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
import uz.alien.dictup.domain.usecase.home.GetAllNativeStoryUseCase
import uz.alien.dictup.domain.usecase.home.GetAllNativeWordUseCase
import uz.alien.dictup.domain.usecase.home.GetAllStoryUseCase
import uz.alien.dictup.domain.usecase.home.GetAllWordsUseCase
import uz.alien.dictup.domain.usecase.home.GetDataStoreRepositoryUseCase
import uz.alien.dictup.domain.usecase.home.GetScoreOfBeginnerUseCase
import uz.alien.dictup.domain.usecase.home.GetScoreOfEssentialUseCase
import uz.alien.dictup.domain.usecase.home.IsSyncCompletedUseCase
import uz.alien.dictup.domain.usecase.home.MainUseCases
import uz.alien.dictup.domain.usecase.on_internet_connected.SyncDataUseCase
import uz.alien.dictup.domain.usecase.pick.GetUnitsPercentUseCase
import uz.alien.dictup.domain.usecase.pick.PickUseCases
import uz.alien.dictup.domain.usecase.startup.CreateUserUseCase
import uz.alien.dictup.domain.usecase.startup.FetchAndActivateUseCase
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
        scoreRepository: ScoreRepository,
        dataStoreRepository: DataStoreRepository
    ): MainUseCases {
        return MainUseCases(
            GetAllWordsUseCase(wordRepository),
            GetAllStoryUseCase(storyRepository),
            GetAllNativeWordUseCase(nativeWordRepository),
            GetAllNativeStoryUseCase(nativeStoryRepository),
            GetScoreOfBeginnerUseCase(scoreRepository),
            GetScoreOfEssentialUseCase(scoreRepository),
            IsSyncCompletedUseCase(dataStoreRepository),
            GetDataStoreRepositoryUseCase(dataStoreRepository)
        )
    }

    @Provides
    fun provideSyncDataUseCaseAndSetupScore(
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
    ): SyncDataUseCase {
        return SyncDataUseCase(
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
    }

    @Provides
    fun provideStartupUseCases(
        userRepository: UserRepository,
        dataStoreRepository: DataStoreRepository,
        remoteConfigRepository: RemoteConfigRepository
    ): StartupUseCases {
        return StartupUseCases(
            CreateUserUseCase(
                userRepository,
                dataStoreRepository
            ),
            FetchAndActivateUseCase(remoteConfigRepository)
        )
    }

    @Provides
    fun providePickUseCases(
        scoreRepository: ScoreRepository
    ): PickUseCases {
        return PickUseCases(
            getUnitsPercentUseCase = GetUnitsPercentUseCase(scoreRepository)
        )
    }
}