package uz.alien.dictup.domain.usecase.home

import uz.alien.dictup.domain.model.Word
import uz.alien.dictup.domain.repository.room.WordRepository

class GetAllWordsUseCase(
    private val wordRepository: WordRepository
) {
    suspend operator fun invoke(): List<Word> {
        return wordRepository.getAllWords()
    }
}