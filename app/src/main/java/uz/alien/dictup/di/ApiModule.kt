package uz.alien.dictup.di

import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
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
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(8, TimeUnit.SECONDS)
            .readTimeout(8, TimeUnit.SECONDS)
            .writeTimeout(8, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideWordApi(retrofit: Retrofit): WordApi =
        retrofit.create(WordApi::class.java)

    @Provides
    fun provideStoryApi(retrofit: Retrofit): StoryApi =
        retrofit.create(StoryApi::class.java)

    @Provides
    fun provideNativeWordApi(retrofit: Retrofit): NativeWordApi =
        retrofit.create(NativeWordApi::class.java)

    @Provides
    fun provideNativeStoryApi(retrofit: Retrofit): NativeStoryApi =
        retrofit.create(NativeStoryApi::class.java)

    @Provides
    fun provideWordRepository(api: WordApi): RemoteWordRepository =
        RemoteWordRepositoryImpl(api)

    @Provides
    fun provideStoryRepository(api: StoryApi): RemoteStoryRepository =
        RemoteStoryRepositoryImpl(api)

    @Provides
    fun provideNativeWordRepository(api: NativeWordApi): RemoteNativeWordRepository =
        RemoteNativeWordRepositoryImpl(api)

    @Provides
    fun provideNativeStoryRepository(api: NativeStoryApi): RemoteNativeStoryRepository =
        RemoteNativeStoryRepositoryImpl(api)
}