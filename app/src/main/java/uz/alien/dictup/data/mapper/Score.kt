package uz.alien.dictup.data.mapper

import uz.alien.dictup.data.local.room.score.ScoreEntity
import uz.alien.dictup.domain.model.Score

fun ScoreEntity.toScore(): Score {
    return Score(
        id = id,
        userId = this.userId,
        wordId = this.wordId,
        correctCount = this.correctCount,
        incorrectCount = this.incorrectCount
    )
}

fun Score.toScoreEntity(): ScoreEntity {
    return ScoreEntity(
        id = id,
        userId = this.userId,
        wordId = this.wordId,
        correctCount = this.correctCount,
        incorrectCount = this.incorrectCount
    )
}
