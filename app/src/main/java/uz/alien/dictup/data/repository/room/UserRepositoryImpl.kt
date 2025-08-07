package uz.alien.dictup.data.repository.room

import uz.alien.dictup.data.local.room.dao.UserDao
import uz.alien.dictup.data.mapper.toUser
import uz.alien.dictup.data.mapper.toUserEntity
import uz.alien.dictup.domain.model.User
import uz.alien.dictup.domain.repository.room.UserRepository

class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun insertUser(user: User): Long {
        return userDao.insertUser(user.toUserEntity())
    }

    override suspend fun getAllUsers(): List<User> {
        return userDao.getAllUsers().map { it.toUser() }
    }

    override suspend fun getUserById(id: Int): User? {
        return userDao.getUserById(id)?.toUser()
    }

    override suspend fun updateUser(user: User) {
        userDao.updateUser(user.toUserEntity())
    }

    override suspend fun deleteUser(user: User) {
        userDao.deleteUser(user.toUserEntity())
    }
}