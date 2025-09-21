package uz.alien.dictup.initializer

import uz.alien.dictup.domain.repository.RemoteConfigRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppInitializer @Inject constructor(
    private val remoteConfigRepository: RemoteConfigRepository
) {
    suspend fun run() {
        remoteConfigRepository.fetchAndActivate()
    }
}