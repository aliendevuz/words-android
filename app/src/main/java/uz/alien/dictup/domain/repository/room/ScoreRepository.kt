package uz.alien.dictup.domain.repository.room

import uz.alien.dictup.domain.model.Score

interface ScoreRepository {

    suspend fun insertScore(score: Score)

    suspend fun insertScores(scores: List<Score>)

    suspend fun updateScore(score: Score)

    suspend fun deleteScore(score: Score)

    suspend fun getScoreById(id: Int): Score?

    suspend fun getAllScores(): List<Score>

    suspend fun clearAllScores()
}