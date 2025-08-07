package uz.alien.dictup.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import uz.alien.dictup.data.local.room.entity.UserEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Int): UserEntity?
}