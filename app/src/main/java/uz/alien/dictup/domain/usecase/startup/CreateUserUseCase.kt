package uz.alien.dictup.domain.usecase.startup

import uz.alien.dictup.domain.model.User
import uz.alien.dictup.domain.repository.room.UserRepository

class CreateUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {
        if (userRepository.getAllUsers().isEmpty()) {
            userRepository.insertUser(User(id = 0, name = "User"))
        }
    }
}