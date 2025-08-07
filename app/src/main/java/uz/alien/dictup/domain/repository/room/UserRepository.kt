package uz.alien.dictup.domain.repository.room

import uz.alien.dictup.domain.model.User

interface UserRepository {

    suspend fun insertUser(user: User): Long

    suspend fun getAllUsers(): List<User>

    suspend fun getUserById(id: Int): User?

    suspend fun updateUser(user: User)

    suspend fun deleteUser(user: User)
}