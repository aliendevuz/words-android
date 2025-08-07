package uz.alien.dictup.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.alien.dictup.data.repository.DataStoreRepositoryImpl
import uz.alien.dictup.domain.repository.DataStoreRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class DataStoreModule {

    @Binds
    abstract fun bindDataStoreRepository(
        impl: DataStoreRepositoryImpl
    ): DataStoreRepository
}