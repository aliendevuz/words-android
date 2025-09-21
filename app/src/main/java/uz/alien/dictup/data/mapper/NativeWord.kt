package uz.alien.dictup.data.mapper

import uz.alien.dictup.data.local.room.entity.NativeWordEntity
import uz.alien.dictup.data.remote.retrofit.dto.WordDto
import uz.alien.dictup.domain.model.NativeWord

fun NativeWordEntity.toNativeWord(): NativeWord {
    return NativeWord(
        id = this.id,
        word = this.word,
        transcription = this.transcription,
        type = this.type,
        definition = this.definition,
        sentence = this.sentence,
        collectionId = this.collectionId,
        partId = this.partId,
        unitId = this.unitId,
        nativeLanguage = this.nativeLanguage
    )
}

fun NativeWord.toNativeWordEntity(): NativeWordEntity {
    return NativeWordEntity(
        id = this.id ?: throw IllegalStateException("NativeWord.id is required to convert to NativeWordEntity"),
        word = this.word,
        transcription = this.transcription,
        type = this.type,
        definition = this.definition,
        sentence = this.sentence,
        collectionId = this.collectionId!!,
        partId = this.partId!!,
        unitId = this.unitId!!,
        nativeLanguage = this.nativeLanguage
    )
}

fun WordDto.toNativeWord(nativeLanguage: String): NativeWord {
    return NativeWord(
        word = this.w,
        transcription = this.tp,
        type = this.t,
        definition = this.d,
        sentence = this.s,
        nativeLanguage = nativeLanguage
    )
}
