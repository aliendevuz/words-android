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
import uz.alien.dictup.domain.usecase.GetDataStoreRepositoryUseCase
import uz.alien.dictup.domain.usecase.GetScoreOfBeginnerUseCase
import uz.alien.dictup.domain.usecase.GetScoreOfEssentialUseCase
import uz.alien.dictup.domain.usecase.GetUnitsPercentUseCase
import uz.alien.dictup.domain.usecase.CreateUserUseCase
import uz.alien.dictup.domain.usecase.FetchAndActivateUseCase
import uz.alien.dictup.domain.usecase.SyncDataUseCase

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideGetScoreOfBeginnerUseCase(
        scoreRepository: ScoreRepository
    ) = GetScoreOfBeginnerUseCase(scoreRepository)

    @Provides
    fun provideGetScoreOfEssentialUseCase(
        scoreRepository: ScoreRepository
    ) = GetScoreOfEssentialUseCase(scoreRepository)

    @Provides
    fun provideGetDataStoreRepositoryUseCase(
        dataStoreRepository: DataStoreRepository
    ) = GetDataStoreRepositoryUseCase(dataStoreRepository)

    @Provides
    fun provideSyncDataUseCase(
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
    fun provideCreateUserUseCase(
        userRepository: UserRepository,
        dataStoreRepository: DataStoreRepository
    ) = CreateUserUseCase(userRepository, dataStoreRepository)

    @Provides
    fun provideFetchAndActivateUseCase(
        remoteConfigRepository: RemoteConfigRepository
    ) = FetchAndActivateUseCase(remoteConfigRepository)

    @Provides
    fun provideGetUnitsPercentUseCase(
        scoreRepository: ScoreRepository
    ) = GetUnitsPercentUseCase(scoreRepository)
}