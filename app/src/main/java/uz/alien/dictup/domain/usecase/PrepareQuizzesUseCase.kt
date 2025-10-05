package uz.alien.dictup.domain.usecase

import uz.alien.dictup.domain.model.Quiz
import uz.alien.dictup.domain.model.Score
import uz.alien.dictup.domain.model.SelectedUnit
import uz.alien.dictup.domain.repository.room.ScoreRepository
import uz.alien.dictup.utils.Logger
import kotlin.random.Random.Default.nextBoolean

class PrepareQuizzesUseCase(
    private val scoreRepository: ScoreRepository
) {

    suspend operator fun invoke(selectedUnits: List<SelectedUnit>, quizCount: Int, quizVariantCount: Int): List<Quiz> {

        val triples = selectedUnits.map { Triple(it.collectionId, it.partId, it.unitId) }

        val scores = scoreRepository.getScoresForUnits(triples)

        val sorted = scores
            .shuffled()
            .sortedWith(
                compareByDescending<Score> { it.incorrectCount }
                .thenBy { it.correctCount }
            )
            .take(quizCount)
            .shuffled()

        val quizzes = sorted.map { score ->

            val otherOptions = scores.toMutableList().apply { remove(score) }

            val wrongOptions = otherOptions
                .shuffled()
                .take(quizVariantCount - 1)
                .map { it.id }

            val options = (wrongOptions + score.id).shuffled()

            Quiz(
                quiz = score.id,
                options = options,
                isQuizNative = nextBoolean()
            )
        }

        return quizzes
    }
}