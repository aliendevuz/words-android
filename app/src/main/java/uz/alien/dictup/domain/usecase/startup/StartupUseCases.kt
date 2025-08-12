package uz.alien.dictup.domain.usecase.startup

data class StartupUseCases(
    val createUserUseCase: CreateUserUseCase,
    val fetchAndActivateUseCase: FetchAndActivateUseCase
)