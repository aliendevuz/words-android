package uz.alien.dictup.data.repository.room

import uz.alien.dictup.data.local.room.dao.NativeStoryDao
import uz.alien.dictup.data.mapper.toNativeStory
import uz.alien.dictup.data.mapper.toNativeStoryEntity
import uz.alien.dictup.domain.model.NativeStory
import uz.alien.dictup.domain.repository.room.NativeStoryRepository

class NativeStoryRepositoryImpl(
    private val nativeStoryDao: NativeStoryDao
) : NativeStoryRepository {

    override suspend fun insertNativeStory(nativeStory: NativeStory) {
        nativeStoryDao.insertNativeStory(nativeStory.toNativeStoryEntity())
    }

    override suspend fun insertNativeStories(nativeStories: List<NativeStory>) {
        nativeStoryDao.insertNativeStories(nativeStories.map { it.toNativeStoryEntity() })
    }

    override suspend fun updateNativeStory(nativeStory: NativeStory) {
        nativeStoryDao.updateNativeStory(nativeStory.toNativeStoryEntity())
    }

    override suspend fun deleteNativeStory(nativeStory: NativeStory) {
        nativeStoryDao.deleteNativeStory(nativeStory.toNativeStoryEntity())
    }

    override suspend fun getNativeStoryById(id: Int): NativeStory? {
        return nativeStoryDao.getNativeStoryById(id)?.toNativeStory()
    }

    override suspend fun getAllNativeStories(): List<NativeStory> {
        return nativeStoryDao.getAllNativeStories().map { it.toNativeStory() }
    }

    override suspend fun clearAllNativeStories() {
        nativeStoryDao.clearAllNativeStories()
    }
}