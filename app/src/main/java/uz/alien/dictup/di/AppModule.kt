package uz.alien.dictup.di

import android.content.Context
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.alien.dictup.data.remote.config.RemoteConfig
import uz.alien.dictup.data.repository.AssetsManagerRepositoryImpl
import uz.alien.dictup.data.repository.CacheManagerRepositoryImpl
import uz.alien.dictup.data.repository.DataStoreRepositoryImpl
import uz.alien.dictup.data.repository.OkHttpManagerImpl
import uz.alien.dictup.data.repository.RemoteConfigRepositoryImpl
import uz.alien.dictup.data.repository.SharedPrefsRepositoryImpl
import uz.alien.dictup.domain.repository.AssetsManagerRepository
import uz.alien.dictup.domain.repository.CacheManagerRepository
import uz.alien.dictup.domain.repository.DataStoreRepository
import uz.alien.dictup.domain.repository.HttpManager
import uz.alien.dictup.domain.repository.RemoteConfigRepository
import uz.alien.dictup.domain.repository.SharedPrefsRepository
import uz.alien.dictup.infrastructure.manager.TTSManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideTTSManager(
        @ApplicationContext context: Context,
        dataStoreRepository: DataStoreRepository
    ): TTSManager {
        return TTSManager(
            context,
            dataStoreRepository
        )
    }

    @Singleton
    @Provides
    fun provideDataStoreRepository(
        @ApplicationContext context: Context
    ): DataStoreRepository {
        return DataStoreRepositoryImpl(context)
    }

    @Singleton
    @Provides
    fun provideSharedPrefsRepository(
        @ApplicationContext context: Context
    ): SharedPrefsRepository {
        return SharedPrefsRepositoryImpl(context)
    }

    @Provides
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig {
        return RemoteConfig.getInstance()
    }

    @Provides
    fun provideRemoteConfigRepository(
        remoteConfig: FirebaseRemoteConfig
    ): RemoteConfigRepository {
        return RemoteConfigRepositoryImpl(remoteConfig)
    }

    @Provides
    fun provideHttpManager(): HttpManager {
        return OkHttpManagerImpl()
    }

    @Provides
    fun provideAssetsManager(
        @ApplicationContext context: Context
    ): AssetsManagerRepository = AssetsManagerRepositoryImpl(
        context
    )

    @Provides
    fun provideCacheManager(
        @ApplicationContext context: Context
    ): CacheManagerRepository = CacheManagerRepositoryImpl(
        context
    )
}