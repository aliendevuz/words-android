package uz.alien.dictup.domain.usecase

import uz.alien.dictup.domain.model.NativeWord
import uz.alien.dictup.domain.repository.room.NativeWordRepository

class GetNativeWordsByUnitUseCase(
    private val nativeWordRepository: NativeWordRepository
) {

    suspend operator fun invoke(collectionId: Int, partId: Int, unitId: Int): List<NativeWord> {
        return nativeWordRepository.getNativeWordsByFullPath(collectionId, partId, unitId)
    }
}