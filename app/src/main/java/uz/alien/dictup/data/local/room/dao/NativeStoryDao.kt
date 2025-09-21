package uz.alien.dictup.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import uz.alien.dictup.data.local.room.entity.NativeStoryEntity

@Dao
interface NativeStoryDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertNativeStory(nativeStory: NativeStoryEntity)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertNativeStories(nativeStories: List<NativeStoryEntity>)

    @Update
    suspend fun updateNativeStory(nativeStory: NativeStoryEntity)

    @Delete
    suspend fun deleteNativeStory(nativeStory: NativeStoryEntity)

    @Query("SELECT * FROM native_stories WHERE id = :id")
    suspend fun getNativeStoryById(id: Int): NativeStoryEntity?

    @Query("SELECT * FROM native_stories")
    suspend fun getAllNativeStories(): List<NativeStoryEntity>

    @Query("SELECT * FROM native_stories WHERE collectionId = :collectionId")
    suspend fun getNativeStoriesByCollectionId(collectionId: Int): List<NativeStoryEntity>

    @Query("DELETE FROM native_stories")
    suspend fun clearAllNativeStories()
}