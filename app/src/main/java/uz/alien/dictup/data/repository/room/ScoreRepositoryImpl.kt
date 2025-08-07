package uz.alien.dictup.data.repository.room

import uz.alien.dictup.data.local.room.dao.ScoreDao
import uz.alien.dictup.data.mapper.toScore
import uz.alien.dictup.data.mapper.toScoreEntity
import uz.alien.dictup.domain.model.Score
import uz.alien.dictup.domain.repository.room.ScoreRepository

class ScoreRepositoryImpl(
    private val scoreDao: ScoreDao
) : ScoreRepository {

    override suspend fun insertScore(score: Score) {
        scoreDao.insertScore(score.toScoreEntity())
    }

    override suspend fun insertScores(scores: List<Score>) {
        scoreDao.insertScores(scores.map { it.toScoreEntity() })
    }

    override suspend fun updateScore(score: Score) {
        scoreDao.updateScore(score.toScoreEntity())
    }

    override suspend fun deleteScore(score: Score) {
        scoreDao.deleteScore(score.toScoreEntity())
    }

    override suspend fun getScoreById(id: Int): Score? {
        return scoreDao.getScoreById(id)?.toScore()
    }

    override suspend fun getAllScores(): List<Score> {
        return scoreDao.getAllScores().map { it.toScore() }
    }

    override suspend fun clearAllScores() {
        scoreDao.clearAllScores()
    }
}