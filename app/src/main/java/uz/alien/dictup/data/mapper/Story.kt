package uz.alien.dictup.data.mapper

import uz.alien.dictup.data.local.room.entity.StoryEntity
import uz.alien.dictup.data.remote.retrofit.dto.StoryDto
import uz.alien.dictup.domain.model.Story

fun StoryEntity.toStory(): Story {
    return Story(
        id = this.id,
        title = this.title,
        content = this.content,
        collectionId = this.collectionId,
        partId = this.partId,
        unitId = this.unitId
    )
}

fun Story.toStoryEntity(): StoryEntity {
    return StoryEntity(
        id = this.id ?: throw IllegalStateException("Story.id is required to convert to StoryEntity"),
        title = this.title,
        content = this.content,
        collectionId = this.collectionId ?: throw IllegalStateException("collectionId is required"),
        partId = this.partId ?: throw IllegalStateException("partId is required"),
        unitId = this.unitId ?: throw IllegalStateException("unitId is required")
    )
}

fun StoryDto.toStory(): Story {
    return Story(
        title = this.h,
        content = this.b
    )
}
