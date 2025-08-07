package uz.alien.dictup.domain.model

data class Score(
    val id: Int,
    val userId: Int,
    val wordId: Int,
    val correctCount: Int,
    val incorrectCount: Int
)