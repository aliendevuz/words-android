package uz.alien.dictup.data.mapper

import uz.alien.dictup.data.local.room.entity.AttemptEntity
import uz.alien.dictup.domain.model.Attempt

fun AttemptEntity.toAttempt(): Attempt {
    return Attempt(
        timestamp = this.timestamp,
        userId = this.userId,
        wordId = this.wordId,
        isCorrect = this.isCorrect
    )
}

fun Attempt.toAttemptEntity(): AttemptEntity {
    return AttemptEntity(
        timestamp = this.timestamp,
        userId = this.userId,
        wordId = this.wordId,
        isCorrect = this.isCorrect
    )
}
