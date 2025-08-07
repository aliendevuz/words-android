package uz.alien.dictup.domain.repository.room

import uz.alien.dictup.domain.model.Word

interface WordRepository {

    suspend fun insertWord(word: Word)

    suspend fun insertWords(words: List<Word>)

    suspend fun updateWord(word: Word)

    suspend fun deleteWord(word: Word)

    suspend fun getWordById(id: Int): Word?

    suspend fun getAllWords(): List<Word>

    suspend fun clearAllWords()
}