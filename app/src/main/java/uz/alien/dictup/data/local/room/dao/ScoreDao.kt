package uz.alien.dictup.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import uz.alien.dictup.data.local.room.entity.ScoreEntity

@Dao
interface ScoreDao {

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertScore(score: ScoreEntity)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertScores(scores: List<ScoreEntity>)

    @Update
    suspend fun updateScore(score: ScoreEntity)

    @Delete
    suspend fun deleteScore(score: ScoreEntity)

    @Query("SELECT * FROM scores WHERE id = :id")
    suspend fun getScoreById(id: Int): ScoreEntity?

    @Query("SELECT * FROM scores WHERE userId = :userId")
    suspend fun getScoresByUserId(userId: Int): List<ScoreEntity>

    @Query("SELECT * FROM scores WHERE wordId = :wordId")
    suspend fun getScoresByWordId(wordId: Int): List<ScoreEntity>

    @Query("SELECT * FROM scores WHERE userId = :userId AND wordId = :wordId")
    suspend fun getScoreByUserAndWord(userId: Int, wordId: Int): ScoreEntity?

    @Query("SELECT * FROM scores")
    suspend fun getAllScores(): List<ScoreEntity>

    @Query("DELETE FROM scores")
    suspend fun clearAllScores()
}