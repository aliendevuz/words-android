package uz.alien.dictup.di

import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uz.alien.dictup.BuildConfig
import uz.alien.dictup.data.remote.retrofit.api.NativeStoryApi
import uz.alien.dictup.data.remote.retrofit.api.NativeWordApi
import uz.alien.dictup.data.remote.retrofit.api.StoryApi
import uz.alien.dictup.data.remote.retrofit.api.WordApi
import uz.alien.dictup.data.repository.retrofit.RemoteNativeStoryRepositoryImpl
import uz.alien.dictup.data.repository.retrofit.RemoteNativeWordRepositoryImpl
import uz.alien.dictup.data.repository.retrofit.RemoteStoryRepositoryImpl
import uz.alien.dictup.data.repository.retrofit.RemoteWordRepositoryImpl
import uz.alien.dictup.domain.repository.retrofit.RemoteNativeStoryRepository
import uz.alien.dictup.domain.repository.retrofit.RemoteNativeWordRepository
import uz.alien.dictup.domain.repository.retrofit.RemoteStoryRepository
import uz.alien.dictup.domain.repository.retrofit.RemoteWordRepository
import uz.alien.dictup.domain.usecase.on_internet_connected.OnInternetConnectedUseCases

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    fun provideWordApi(): WordApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WordApi::class.java)
    }

    @Provides
    fun provideStoryApi(): StoryApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StoryApi::class.java)
    }

    @Provides
    fun provideNativeWordApi(): NativeWordApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NativeWordApi::class.java)
    }

    @Provides
    fun provideNativeStoryApi(): NativeStoryApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NativeStoryApi::class.java)
    }

    @Provides
    fun provideWordRepository(api: WordApi): RemoteWordRepository {
        return RemoteWordRepositoryImpl(api)
    }

    @Provides
    fun provideStoryRepository(api: StoryApi): RemoteStoryRepository {
        return RemoteStoryRepositoryImpl(api)
    }

    @Provides
    fun provideNativeWordRepository(api: NativeWordApi): RemoteNativeWordRepository {
        return RemoteNativeWordRepositoryImpl(api)
    }

    @Provides
    fun provideNativeStoryRepository(api: NativeStoryApi): RemoteNativeStoryRepository {
        return RemoteNativeStoryRepositoryImpl(api)
    }
}