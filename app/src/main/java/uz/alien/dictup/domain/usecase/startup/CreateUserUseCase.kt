package uz.alien.dictup.domain.usecase.startup

import uz.alien.dictup.domain.model.User
import uz.alien.dictup.domain.repository.DataStoreRepository
import uz.alien.dictup.domain.repository.room.UserRepository

class CreateUserUseCase(
    private val userRepository: UserRepository,
    private val dataStoreRepository: DataStoreRepository
) {
    suspend operator fun invoke() {
        if (userRepository.getAllUsers().isEmpty()) {
            userRepository.insertUser(User(id = 1, name = "User"))
            dataStoreRepository.saveCurrentUserId(1)
        }
    }
}