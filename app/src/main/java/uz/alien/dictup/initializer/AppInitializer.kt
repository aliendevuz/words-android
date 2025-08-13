package uz.alien.dictup.initializer

import uz.alien.dictup.domain.usecase.CreateUserUseCase
import uz.alien.dictup.domain.usecase.FetchAndActivateUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppInitializer @Inject constructor(
    private val createUserUseCase: CreateUserUseCase,
    private val fetchAndActivateUseCase: FetchAndActivateUseCase
) {
    suspend fun run() {
        createUserUseCase()
        fetchAndActivateUseCase()
    }
}