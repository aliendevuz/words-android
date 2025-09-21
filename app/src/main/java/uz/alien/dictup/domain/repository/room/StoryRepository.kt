package uz.alien.dictup.domain.repository.room

import uz.alien.dictup.domain.model.Story

interface StoryRepository {

    suspend fun insertStory(story: Story)

    suspend fun insertStories(stories: List<Story>)

    suspend fun updateStory(story: Story)

    suspend fun deleteStory(story: Story)

    suspend fun getStoryById(id: Int): Story?

    suspend fun getAllStories(): List<Story>

    suspend fun getStoriesByUnit(collectionId: Int, partId: Int, unitId: Int): List<Story>

    suspend fun getStoriesByCollectionId(collectionId: Int): List<Story>

    suspend fun clearAllStories()
}