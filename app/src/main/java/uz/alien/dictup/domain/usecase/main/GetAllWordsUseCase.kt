package uz.alien.dictup.domain.usecase.main

import uz.alien.dictup.domain.model.Word
import uz.alien.dictup.domain.repository.room.NativeStoryRepository
import uz.alien.dictup.domain.repository.room.NativeWordRepository
import uz.alien.dictup.domain.repository.room.ScoreRepository
import uz.alien.dictup.domain.repository.room.WordRepository

class GetAllWordsUseCase(
    private val wordRepository: WordRepository
) {
    suspend operator fun invoke(): List<Word> {
        return wordRepository.getAllWords()
    }
}