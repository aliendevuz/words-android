package uz.alien.dictup.domain.usecase

import uz.alien.dictup.domain.model.Score
import uz.alien.dictup.domain.model.SelectedUnit
import uz.alien.dictup.domain.repository.room.ScoreRepository

class PrepareQuizzesUseCase(
    private val scoreRepository: ScoreRepository
) {

//    suspend operator fun invoke(currentUserId: Int, quizVariantCount: Int, quizCount: Int, selectedUnits: List<SelectedUnit>): List<Quiz> {
//
//        val selectedScores = mutableListOf<Score>()
//
//        selectedUnits.forEach { it ->
//            scoreRepository.getScoresByFullPath(currentUserId, it.collectionId, it.partId, it.unitId).forEach {
//                selectedScores.add(it)
//            }
//        }
//
//        val selectedNativeWordsScores = selectedScores
//            .sortedWith(
//                compareBy { it.correctCount - it.incorrectCount }
//            )
//
//        val quizScores = selectedNativeWordsScores
//            .take(quizCount)
//            .shuffled()
//
//        val quizzes = mutableListOf<Quiz>()
//
//        quizScores.forEach { score ->
//
//            fun getQuiz(scoreId: Int): Quiz {
//                val answers = mutableListOf<Int>()
//                return Quiz(scoreId, answers)
//            }
//
//            quizzes += getQuiz(score.id)
//        }
//
//        return quizzes
//    }
}