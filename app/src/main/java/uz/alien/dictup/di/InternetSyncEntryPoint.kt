package uz.alien.dictup.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.alien.dictup.domain.usecase.on_internet_connected.SyncDataUseCase

@EntryPoint
@InstallIn(SingletonComponent::class)
interface InternetSyncEntryPoint {
    fun syncDataUseCaseAndSetupScore(): SyncDataUseCase
}