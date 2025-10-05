package uz.alien.dictup.infrastructure.shared

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.alien.dictup.domain.usecase.sync.SyncDataUseCase

@EntryPoint
@InstallIn(SingletonComponent::class)
interface InternetSyncEntryPoint {
    fun syncDataUseCaseAndSetupScore(): SyncDataUseCase
}