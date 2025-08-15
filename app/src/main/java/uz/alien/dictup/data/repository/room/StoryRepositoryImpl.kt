package uz.alien.dictup.data.repository.room

import uz.alien.dictup.data.local.room.dao.StoryDao
import uz.alien.dictup.data.mapper.toStory
import uz.alien.dictup.data.mapper.toStoryEntity
import uz.alien.dictup.domain.model.Story
import uz.alien.dictup.domain.repository.room.StoryRepository

class StoryRepositoryImpl(
    private val storyDao: StoryDao
) : StoryRepository {

    override suspend fun insertStory(story: Story) {
        storyDao.insertStory(story.toStoryEntity())
    }

    override suspend fun insertStories(stories: List<Story>) {
        storyDao.insertStories(stories.map { it.toStoryEntity() })
    }

    override suspend fun updateStory(story: Story) {
        storyDao.updateStory(story.toStoryEntity())
    }

    override suspend fun deleteStory(story: Story) {
        storyDao.deleteStory(story.toStoryEntity())
    }

    override suspend fun getStoryById(id: Int): Story? {
        return storyDao.getStoryById(id)?.toStory()
    }

    override suspend fun getAllStories(): List<Story> {
        return storyDao.getAllStories().map { it.toStory() }
    }

    override suspend fun getStoriesByUnit(
        collectionId: Int,
        partId: Int,
        unitId: Int
    ): List<Story> {
        return storyDao.getStoriesByFullPath(collectionId, partId, unitId)
    }

    override suspend fun clearAllStories() {
        storyDao.clearAllStories()
    }
}