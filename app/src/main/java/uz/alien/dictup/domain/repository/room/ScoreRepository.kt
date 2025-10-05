package uz.alien.dictup.domain.repository.room

import uz.alien.dictup.domain.model.Score

interface ScoreRepository {

    suspend fun insertScore(score: Score)

    suspend fun insertScores(scores: List<Score>)

    suspend fun updateScore(score: Score)

    suspend fun deleteScore(score: Score)

    suspend fun getScoreById(id: Int): Score?

    suspend fun getAllScores(): List<Score>

    suspend fun getScoresByCollectionId(collectionId: Int): List<Score>

    suspend fun getScoresByCollectionAndPart(
        collectionId: Int,
        partId: Int
    ): List<Score>

    suspend fun getScoresByFullPath(
        collectionId: Int,
        partId: Int,
        unitId: Int
    ): List<Score>

    suspend fun getScoresForUnits(triples: List<Triple<Int, Int, Int>>): List<Score>

    suspend fun clearAllScores()
}