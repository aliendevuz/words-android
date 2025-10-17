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

    suspend operator fun invoke(
        selectedUnits: List<SelectedUnit>,
        quizCount: Int,
        quizVariantCount: Int
    ): List<Quiz> {
        val triples = selectedUnits.map { Triple(it.collectionId, it.partId, it.unitId) }

        val scores = scoreRepository.getScoresForUnits(triples)

        // 1️⃣ Saralash: ko‘p xatolarga ega so‘zlar birinchi bo‘ladi
        val sorted = scores
            .shuffled()
            .sortedWith(
                compareByDescending { it.incorrectCount + it.correctCount }
            )
            .take(quizCount)
            .shuffled()

        // 2️⃣ Har bir quiz uchun variantlar yaratish
        val quizzes = sorted.map { correctScore ->

            // Shu to‘g‘ri javobni chiqarib tashlagan holatda noto‘g‘ri variantlar tanlash
            val wrongOptions = scores
                .filter { it.id != correctScore.id }  // <-- safe remove
                .shuffled()
                .take(quizVariantCount - 1)
                .map { it.id }

            // To‘g‘ri + noto‘g‘ri variantlarni aralashtirish
            val options = (wrongOptions + correctScore.id).shuffled()

            Quiz(
                quiz = correctScore.id,
                options = options,
                isQuizNative = nextBoolean()
            )
        }

        return quizzes
    }
}