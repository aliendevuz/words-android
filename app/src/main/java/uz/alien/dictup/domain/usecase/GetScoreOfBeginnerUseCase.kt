package uz.alien.dictup.domain.usecase

import uz.alien.dictup.domain.repository.room.ScoreRepository

class GetScoreOfBeginnerUseCase(
    private val scoreRepository: ScoreRepository
) {

    suspend operator fun invoke(): List<Int> {
        return (0..3).map { part ->
            val scores = scoreRepository.getScoresByCollectionAndPart(0, part)
            if (scores.isEmpty()) {
                0
            } else {
                val progress = scores.count { it.correctCount - it.incorrectCount > 5 }
                ((progress / scores.size.toFloat()) * 100).toInt()
            }
        }
    }
}