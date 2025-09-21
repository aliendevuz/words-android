package uz.alien.dictup.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.alien.dictup.domain.repository.AssetsManagerRepository
import uz.alien.dictup.domain.repository.CacheManagerRepository
import uz.alien.dictup.domain.repository.DataStoreRepository
import uz.alien.dictup.domain.repository.HttpManager
import uz.alien.dictup.domain.repository.SharedPrefsRepository
import uz.alien.dictup.domain.repository.retrofit.RemoteNativeStoryRepository
import uz.alien.dictup.domain.repository.retrofit.RemoteNativeWordRepository
import uz.alien.dictup.domain.repository.retrofit.RemoteStoryRepository
import uz.alien.dictup.domain.repository.retrofit.RemoteWordRepository
import uz.alien.dictup.domain.repository.room.NativeStoryRepository
import uz.alien.dictup.domain.repository.room.NativeWordRepository
import uz.alien.dictup.domain.repository.room.ScoreRepository
import uz.alien.dictup.domain.repository.room.StoryRepository
import uz.alien.dictup.domain.repository.room.WordRepository
import uz.alien.dictup.domain.usecase.GetScoreOfBeginnerUseCase
import uz.alien.dictup.domain.usecase.GetScoreOfEssentialUseCase
import uz.alien.dictup.domain.usecase.GetUnitsPercentUseCase
import uz.alien.dictup.domain.usecase.PrepareQuizzesUseCase
import uz.alien.dictup.domain.usecase.SyncDataUseCase
import uz.alien.dictup.domain.usecase.sync.UpdateUseCase

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
        scoreRepository: ScoreRepository,
        prefsRepository: SharedPrefsRepository,
        httpManager: HttpManager,
        cacheManagerRepository: CacheManagerRepository
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
            scoreRepository = scoreRepository,
            prefsRepository = prefsRepository,
            httpManager = httpManager,
            cacheManagerRepository = cacheManagerRepository
        )
    }

    @Provides
    fun provideGetUnitsPercentUseCase(
        scoreRepository: ScoreRepository
    ) = GetUnitsPercentUseCase(scoreRepository)

    @Provides
    fun providePrepareQuizzesUseCase(
        scoreRepository: ScoreRepository
    ) = PrepareQuizzesUseCase(
        scoreRepository = scoreRepository
    )

    @Provides
    fun provideAssetSyncUseCase(
        wordRepository: WordRepository,
        dataStoreRepository: DataStoreRepository,
        assetsManagerRepository: AssetsManagerRepository,
        cacheManagerRepository: CacheManagerRepository
    ) = UpdateUseCase(
        wordRepository = wordRepository,
        dataStoreRepository = dataStoreRepository,
        assetsManagerRepository = assetsManagerRepository,
        cacheManagerRepository = cacheManagerRepository
    )
}