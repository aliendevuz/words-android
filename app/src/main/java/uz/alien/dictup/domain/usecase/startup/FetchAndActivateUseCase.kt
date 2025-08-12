package uz.alien.dictup.domain.usecase.startup

import uz.alien.dictup.domain.repository.RemoteConfigRepository

class FetchAndActivateUseCase(
    private val remoteConfigRepository: RemoteConfigRepository
) {

    suspend operator fun invoke() {
        remoteConfigRepository.fetchAndActivate()
    }
}