package uz.alien.dictup.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import uz.alien.dictup.data.local.room.entity.AttemptEntity

@Dao
interface AttemptDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAttempt(attempt: AttemptEntity)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAttempts(attempts: List<AttemptEntity>)

    @Delete
    suspend fun deleteAttempt(attempt: AttemptEntity)

    @Query("SELECT * FROM attempts WHERE timestamp = :timestamp")
    suspend fun getAttemptByTimestamp(timestamp: Long): AttemptEntity?

    @Query("SELECT * FROM attempts WHERE userId = :userId")
    suspend fun getAttemptsByUser(userId: Int): List<AttemptEntity>

    @Query("SELECT * FROM attempts WHERE wordId = :wordId")
    suspend fun getAttemptsByWord(wordId: Int): List<AttemptEntity>

    @Query("SELECT * FROM attempts WHERE userId = :userId AND wordId = :wordId")
    suspend fun getAttemptsByUserAndWord(userId: Int, wordId: Int): List<AttemptEntity>

    @Query("SELECT * FROM attempts")
    suspend fun getAllAttempts(): List<AttemptEntity>

    @Query("DELETE FROM attempts")
    suspend fun clearAllAttempts()
}