package uz.alien.dictup.domain.usecase

import uz.alien.dictup.domain.repository.room.ScoreRepository
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class GetUnitsPercentUseCase(
    private val scoreRepository: ScoreRepository
) {

    suspend operator fun invoke(collectionId: Int, partId: Int): List<Int> {
        val scoresByWord = scoreRepository.getScoresByCollectionAndPart(collectionId, partId)

        return scoresByWord
            .chunked(20)
            .map { unitScores ->
                var progress = 0f
                unitScores.forEach {
                    if (it.correctCount - it.incorrectCount > 0) {
                        progress += min(it.correctCount - it.incorrectCount, 5)
                    }
                }
                (progress / unitScores.size / 5 * 100).roundToInt()
            }
    }
}