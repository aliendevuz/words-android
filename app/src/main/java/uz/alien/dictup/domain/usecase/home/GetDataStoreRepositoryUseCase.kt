package uz.alien.dictup.domain.usecase.home

import uz.alien.dictup.domain.repository.DataStoreRepository

class GetDataStoreRepositoryUseCase(
    private val dataStoreRepository: DataStoreRepository
) {

    operator fun invoke(): DataStoreRepository {
        return dataStoreRepository
    }
}