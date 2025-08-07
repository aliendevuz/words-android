package uz.alien.dictup.domain.usecase.main

import uz.alien.dictup.domain.model.Score
import uz.alien.dictup.domain.repository.room.ScoreRepository

class GetAllScoreUseCase(
    private val scoreRepository: ScoreRepository
) {
    suspend operator fun invoke(): List<Score> {
        val scores = scoreRepository.getAllScores().filter {
            it.correctCount != 0 || it.incorrectCount != 0
        }
        return scores
    }
}