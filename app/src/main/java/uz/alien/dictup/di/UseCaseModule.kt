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
import uz.alien.dictup.domain.repository.room.NativeStoryRepository
import uz.alien.dictup.domain.repository.room.NativeWordRepository
import uz.alien.dictup.domain.repository.room.ScoreRepository
import uz.alien.dictup.domain.repository.room.StoryRepository
import uz.alien.dictup.domain.repository.room.WordRepository
import uz.alien.dictup.domain.usecase.GetScoreOfBeginnerUseCase
import uz.alien.dictup.domain.usecase.GetScoreOfEssentialUseCase
import uz.alien.dictup.domain.usecase.GetUnitsPercentUseCase
import uz.alien.dictup.domain.usecase.PrepareQuizzesUseCase
import uz.alien.dictup.domain.usecase.sync.SyncDataUseCase
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
        dataStoreRepository: DataStoreRepository,
        httpManager: HttpManager,
        cacheManagerRepository: CacheManagerRepository
    ): SyncDataUseCase {
        return SyncDataUseCase(
            dataStoreRepository = dataStoreRepository,
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
        @ApplicationContext context: Context,
        wordRepository: WordRepository,
        nativeWordRepository: NativeWordRepository,
        storyRepository: StoryRepository,
        nativeStoryRepository: NativeStoryRepository,
        scoreRepository: ScoreRepository,
        dataStoreRepository: DataStoreRepository,
        assetsManagerRepository: AssetsManagerRepository,
        cacheManagerRepository: CacheManagerRepository
    ) = UpdateUseCase(
        context = context,
        wordRepository = wordRepository,
        nativeWordRepository = nativeWordRepository,
        storyRepository = storyRepository,
        nativeStoryRepository = nativeStoryRepository,
        scoreRepository = scoreRepository,
        dataStoreRepository = dataStoreRepository,
        assetsManagerRepository = assetsManagerRepository,
        cacheManagerRepository = cacheManagerRepository
    )
}