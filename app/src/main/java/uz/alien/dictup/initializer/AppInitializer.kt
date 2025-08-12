package uz.alien.dictup.initializer

import uz.alien.dictup.domain.usecase.startup.StartupUseCases
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppInitializer @Inject constructor(
    private val startupUseCases: StartupUseCases

) {
    suspend fun run() {
        startupUseCases.createUserUseCase()
        startupUseCases.fetchAndActivateUseCase()
    }
}