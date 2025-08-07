package uz.alien.dictup.data.mapper

import uz.alien.dictup.data.local.room.entity.WordEntity
import uz.alien.dictup.data.remote.retrofit.dto.WordDto
import uz.alien.dictup.domain.model.Word

fun WordEntity.toWord() : Word {
    return Word(
        id = this.id,
        word = this.word,
        transcription = this.transcription,
        type = this.type,
        definition = this.definition,
        sentence = this.sentence,
        collectionId = this.collectionId,
        partId = this.partId,
        unitId = this.unitId
    )
}

fun Word.toWordEntity(): WordEntity {
    return WordEntity(
        id = this.id ?: throw IllegalStateException("Word.id is required to convert to WordEntity"),
        word = word,
        transcription = transcription,
        type = type,
        definition = definition,
        sentence = sentence,
        collectionId = this.collectionId ?: throw IllegalStateException("collectionId is required"),
        partId = this.partId ?: throw IllegalStateException("partId is required"),
        unitId = this.unitId ?: throw IllegalStateException("unitId is required")
    )
}

fun WordDto.toWord() : Word {
    return Word(
        word = this.w,
        transcription = this.tp,
        type = this.t,
        definition = this.d,
        sentence = this.s
    )
}
