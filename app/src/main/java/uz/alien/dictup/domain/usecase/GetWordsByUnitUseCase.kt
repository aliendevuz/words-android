package uz.alien.dictup.domain.usecase

import uz.alien.dictup.domain.model.Word
import uz.alien.dictup.domain.repository.room.WordRepository

class GetWordsByUnitUseCase(
    private val wordRepository: WordRepository
) {

    suspend operator fun invoke(collectionId: Int, partId: Int, unitId: Int): List<Word> {
        return wordRepository.getWordsByFullPath(collectionId, partId, unitId)
    }
}