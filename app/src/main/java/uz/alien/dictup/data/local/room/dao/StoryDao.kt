package uz.alien.dictup.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import uz.alien.dictup.data.local.room.entity.StoryEntity
import uz.alien.dictup.domain.model.Story

@Dao
interface StoryDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertStory(story: StoryEntity)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertStories(stories: List<StoryEntity>)

    @Update
    suspend fun updateStory(story: StoryEntity)

    @Delete
    suspend fun deleteStory(story: StoryEntity)

    @Query("SELECT * FROM stories WHERE id = :id")
    suspend fun getStoryById(id: Int): StoryEntity?

    @Query("SELECT * FROM stories")
    suspend fun getAllStories(): List<StoryEntity>

    @Query("SELECT * FROM stories WHERE collectionId = :collectionId AND partId = :partId AND unitId = :unitId")
    suspend fun getStoriesByFullPath(collectionId: Int, partId: Int, unitId: Int): List<Story>

    @Query("DELETE FROM stories")
    suspend fun clearAllStories()
}