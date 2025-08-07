package uz.alien.dictup.domain.model

data class Attempt(
    val timestamp: Long,
    val userId: Int,
    val wordId: Int,
    val isCorrect: Boolean
)