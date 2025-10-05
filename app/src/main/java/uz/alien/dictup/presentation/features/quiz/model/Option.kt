package uz.alien.dictup.presentation.features.quiz.model


enum class Status {
    NOT_ANSWERED,
    WRONG,
    CORRECT
}

data class Option(
    val id: Int,
    val wordId: Int,
    val name: String = "",
    val status: Status = Status.NOT_ANSWERED
)