package uz.alien.dictup.domain.usecase.home

import kotlinx.coroutines.flow.Flow
import uz.alien.dictup.domain.repository.DataStoreRepository

class IsSyncCompletedUseCase(
    private val dataStoreRepository: DataStoreRepository
) {

    suspend operator fun invoke(): Flow<Boolean> {
        return dataStoreRepository.isSyncCompleted()
    }
}