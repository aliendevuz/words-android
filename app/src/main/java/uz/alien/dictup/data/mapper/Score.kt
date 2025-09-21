package uz.alien.dictup.data.mapper

import uz.alien.dictup.data.local.room.entity.ScoreEntity
import uz.alien.dictup.domain.model.Score

fun ScoreEntity.toScore(): Score {
    return Score(
        id = id,
        nativeWordId = this.nativeWordId,
        correctCount = this.correctCount,
        incorrectCount = this.incorrectCount,
        collectionId = this.collectionId,
        partId = this.partId,
        unitId = this.unitId
    )
}

fun Score.toScoreEntity(): ScoreEntity {
    return ScoreEntity(
        id = id,
        collectionId = this.collectionId!!,
        partId = this.partId!!,
        unitId = this.unitId!!,
        correctCount = this.correctCount,
        incorrectCount = this.incorrectCount,
        nativeWordId = this.nativeWordId!!
    )
}
