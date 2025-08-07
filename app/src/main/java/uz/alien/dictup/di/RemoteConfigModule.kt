package uz.alien.dictup.di

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.alien.dictup.data.repository.RemoteConfigRepositoryImpl
import uz.alien.dictup.data.remote.config.RemoteConfig
import uz.alien.dictup.domain.repository.RemoteConfigRepository

@Module
@InstallIn(SingletonComponent::class)
object RemoteConfigModule {

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