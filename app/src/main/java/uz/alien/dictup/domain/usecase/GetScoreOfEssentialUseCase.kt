package uz.alien.dictup.domain.usecase

import uz.alien.dictup.domain.repository.room.ScoreRepository
import uz.alien.dictup.utils.Logger
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class GetScoreOfEssentialUseCase(
    private val scoreRepository: ScoreRepository
) {

    suspend operator fun invoke(): List<Int> {
        return (0..5).map { part ->
            val scores = scoreRepository.getScoresByCollectionAndPart(1, part)
            var progress = 0f
            if (scores.isEmpty()) {
                Logger.d(GetScoreOfEssentialUseCase::class.java.simpleName, "scores is empty")
            } else {
                scores.forEach {
                    if(it.correctCount - it.incorrectCount > 0) {
                        progress += min(it.correctCount - it.incorrectCount, 5)
                    }
                }
                progress = progress / scores.size / 5 * 100
            }
            progress.roundToInt()
        }
    }
}