package uz.alien.dictup.domain.repository.room

import uz.alien.dictup.domain.model.NativeWord

interface NativeWordRepository {

    suspend fun insertNativeWord(nativeWord: NativeWord)

    suspend fun insertNativeWords(nativeWords: List<NativeWord>)

    suspend fun updateNativeWord(nativeWord: NativeWord)

    suspend fun deleteNativeWord(nativeWord: NativeWord)

    suspend fun getNativeWordById(id: Int): NativeWord?

    suspend fun getAllNativeWords(): List<NativeWord>

    suspend fun getNativeWordsByFullPath(
        collectionId: Int,
        partId: Int,
        unitId: Int
    ): List<NativeWord>

    suspend fun clearAllNativeWords()
}