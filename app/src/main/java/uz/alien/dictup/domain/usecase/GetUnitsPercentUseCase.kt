package uz.alien.dictup.domain.usecase

import uz.alien.dictup.domain.repository.room.ScoreRepository

class GetUnitsPercentUseCase(
    private val scoreRepository: ScoreRepository
) {

    suspend operator fun invoke(collectionId: Int, partId: Int): List<Int> {
        val scoresByUnit = scoreRepository.getScoresByCollectionAndPart(collectionId, partId)
        return scoresByUnit.map { scores ->
            val correctCount = scores.correctCount
            val incorrectCount = scores.incorrectCount
            val progressValue = ((correctCount - incorrectCount) / (correctCount + incorrectCount).toFloat()) * 100
            progressValue.toInt()
        }
    }
}