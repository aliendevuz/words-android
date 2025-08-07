package uz.alien.dictup.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uz.alien.dictup.BuildConfig
import uz.alien.dictup.data.remote.retrofit.api.WordApi
import uz.alien.dictup.data.repository.retrofit.RemoteWordRepositoryImpl
import uz.alien.dictup.domain.repository.retrofit.RemoteWordRepository

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
    fun provideWordRepository(api: WordApi): RemoteWordRepository {
        return RemoteWordRepositoryImpl(api)
    }
}