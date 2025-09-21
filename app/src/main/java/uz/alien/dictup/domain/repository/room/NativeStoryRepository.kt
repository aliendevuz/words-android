package uz.alien.dictup.domain.repository.room

import uz.alien.dictup.domain.model.NativeStory

interface NativeStoryRepository {

    suspend fun insertNativeStory(nativeStory: NativeStory)

    suspend fun insertNativeStories(nativeStories: List<NativeStory>)

    suspend fun updateNativeStory(nativeStory: NativeStory)

    suspend fun deleteNativeStory(nativeStory: NativeStory)

    suspend fun getNativeStoryById(id: Int): NativeStory?

    suspend fun getAllNativeStories(): List<NativeStory>

    suspend fun getNativeStoriesByCollectionId(collectionId: Int): List<NativeStory>

    suspend fun clearAllNativeStories()
}