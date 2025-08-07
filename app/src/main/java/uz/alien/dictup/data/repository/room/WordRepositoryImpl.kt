package uz.alien.dictup.data.repository.room

import uz.alien.dictup.data.local.room.dao.WordDao
import uz.alien.dictup.data.mapper.toWord
import uz.alien.dictup.data.mapper.toWordEntity
import uz.alien.dictup.domain.model.Word
import uz.alien.dictup.domain.repository.room.WordRepository

class WordRepositoryImpl(
    private val wordDao: WordDao
) : WordRepository {

    override suspend fun insertWord(word: Word) {
        wordDao.insertWord(word.toWordEntity())
    }

    override suspend fun insertWords(words: List<Word>) {
        wordDao.insertWords(words.map { it.toWordEntity() })
    }

    override suspend fun updateWord(word: Word) {
        wordDao.updateWord(word.toWordEntity())
    }

    override suspend fun deleteWord(word: Word) {
        wordDao.deleteWord(word.toWordEntity())
    }

    override suspend fun getWordById(id: Int): Word? {
        return wordDao.getWordById(id)?.toWord()
    }

    override suspend fun getAllWords(): List<Word> {
        return wordDao.getAllWords().map { it.toWord() }
    }

    override suspend fun clearAllWords() {
        wordDao.clearAllWords()
    }
}