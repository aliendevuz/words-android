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

    @Query("SELECT * FROM scores")
    suspend fun getScoresByUserId(): List<ScoreEntity>

    @Query("SELECT * FROM scores WHERE nativeWordId = :wordId")
    suspend fun getScoresByWordId(wordId: Int): List<ScoreEntity>

    @Query("SELECT * FROM scores WHERE nativeWordId = :wordId")
    suspend fun getScoreByUserAndWord(wordId: Int): ScoreEntity?

    @Query("SELECT * FROM scores")
    suspend fun getAllScores(): List<ScoreEntity>

    @Query("DELETE FROM scores")
    suspend fun clearAllScores()

    @Query("SELECT * FROM scores WHERE collectionId = :collectionId")
    suspend fun getScoresByCollectionId(collectionId: Int): List<ScoreEntity>

    @Query("SELECT * FROM scores WHERE collectionId = :collectionId AND partId = :partId")
    suspend fun getScoresByCollectionAndPart(
        collectionId: Int,
        partId: Int
    ): List<ScoreEntity>

    @Query("SELECT * FROM scores WHERE collectionId = :collectionId AND partId = :partId AND unitId = :unitId")
    suspend fun getScoresByFullPath(
        collectionId: Int,
        partId: Int,
        unitId: Int
    ): List<ScoreEntity>

    @Transaction
    suspend fun getScoresForUnits(
        triples: List<Triple<Int, Int, Int>>
    ): List<ScoreEntity> {
        val results = mutableListOf<ScoreEntity>()
        for ((c, p, u) in triples) {
            results += getScoresByFullPath(c, p, u)
        }
        return results
    }
}