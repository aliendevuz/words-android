package uz.alien.dictup.data.local.room.native_story

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface NativeStoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNativeStory(nativeStory: NativeStoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNativeStories(nativeStories: List<NativeStoryEntity>)

    @Update
    suspend fun updateNativeStory(nativeStory: NativeStoryEntity)

    @Delete
    suspend fun deleteNativeStory(nativeStory: NativeStoryEntity)

    @Query("SELECT * FROM native_stories WHERE id = :id")
    suspend fun getNativeStoryById(id: Int): NativeStoryEntity?

    @Query("SELECT * FROM native_stories WHERE storyId = :storyId")
    suspend fun getNativeStoriesByStoryId(storyId: Int): List<NativeStoryEntity>

    @Query("SELECT * FROM native_stories")
    suspend fun getAllNativeStories(): List<NativeStoryEntity>

    @Query("DELETE FROM native_stories")
    suspend fun clearAllNativeStories()
}