package uz.alien.dictup.data.repository.room

import uz.alien.dictup.data.local.room.dao.NativeWordDao
import uz.alien.dictup.data.mapper.toNativeWord
import uz.alien.dictup.data.mapper.toNativeWordEntity
import uz.alien.dictup.domain.model.NativeWord
import uz.alien.dictup.domain.repository.room.NativeWordRepository

class NativeWordRepositoryImpl(
    private val nativeWordDao: NativeWordDao
) : NativeWordRepository {

    override suspend fun insertNativeWord(nativeWord: NativeWord) {
        nativeWordDao.insertNativeWord(nativeWord.toNativeWordEntity())
    }

    override suspend fun insertNativeWords(nativeWords: List<NativeWord>) {
        nativeWordDao.insertNativeWords(nativeWords.map { it.toNativeWordEntity() })
    }

    override suspend fun updateNativeWord(nativeWord: NativeWord) {
        nativeWordDao.updateNativeWord(nativeWord.toNativeWordEntity())
    }

    override suspend fun deleteNativeWord(nativeWord: NativeWord) {
        nativeWordDao.deleteNativeWord(nativeWord.toNativeWordEntity())
    }

    override suspend fun getNativeWordById(id: Int): NativeWord? {
        return nativeWordDao.getNativeWordById(id)?.toNativeWord()
    }

    override suspend fun getAllNativeWords(): List<NativeWord> {
        return nativeWordDao.getAllNativeWords().map { it.toNativeWord() }
    }

    override suspend fun getNativeWordsByCollectionId(collectionId: Int): List<NativeWord> {
        return nativeWordDao.getNativeWordsByCollectionId(collectionId)
            .map { it.toNativeWord() }
    }

    override suspend fun getNativeWordsByFullPath(
        collectionId: Int,
        partId: Int,
        unitId: Int
    ): List<NativeWord> {
        return nativeWordDao.getNativeWordsByFullPath(collectionId, partId, unitId)
            .map { it.toNativeWord() }
    }

    override suspend fun clearAllNativeWords() {
        nativeWordDao.clearAllNativeWords()
    }
}