package uz.alien.dictup.di

import android.content.Context
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.alien.dictup.data.remote.config.RemoteConfig
import uz.alien.dictup.data.repository.DataStoreRepositoryImpl
import uz.alien.dictup.data.repository.RemoteConfigRepositoryImpl
import uz.alien.dictup.domain.repository.DataStoreRepository
import uz.alien.dictup.domain.repository.RemoteConfigRepository
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
}