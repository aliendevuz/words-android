package uz.alien.dictup.data.mapper

import uz.alien.dictup.data.local.room.entity.NativeStoryEntity
import uz.alien.dictup.data.remote.retrofit.dto.StoryDto
import uz.alien.dictup.domain.model.NativeStory

fun NativeStoryEntity.toNativeStory(): NativeStory {
    return NativeStory(
        id = this.id,
        title = this.title,
        content = this.content,
        collectionId = this.collectionId,
        partId = this.partId,
        unitId = this.unitId,
        nativeLanguage = this.nativeLanguage
    )
}

fun NativeStory.toNativeStoryEntity(): NativeStoryEntity {
    return NativeStoryEntity(
        id = this.id ?: throw IllegalStateException("NativeStory.id is required to convert to NativeStoryEntity"),
        title = this.title,
        content = this.content,
        collectionId = this.collectionId!!,
        partId = this.partId!!,
        unitId = this.unitId!!,
        nativeLanguage = this.nativeLanguage
    )
}

fun StoryDto.toNativeStory(nativeLanguage: String): NativeStory {
    return NativeStory(
        title = this.h,
        content = this.b,
        nativeLanguage = nativeLanguage
    )
}
