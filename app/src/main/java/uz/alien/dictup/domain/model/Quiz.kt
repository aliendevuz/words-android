package uz.alien.dictup.domain.model

data class Quiz(
    val quiz: Int,
    val options: List<Int>,
    val isQuizNative: Boolean,
    val isCorrect: Boolean = false
)
