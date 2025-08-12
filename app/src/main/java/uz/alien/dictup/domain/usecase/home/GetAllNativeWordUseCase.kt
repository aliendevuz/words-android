package uz.alien.dictup.domain.usecase.home

import uz.alien.dictup.domain.model.NativeWord
import uz.alien.dictup.domain.repository.room.NativeWordRepository

class GetAllNativeWordUseCase(
    private val nativeWordRepository: NativeWordRepository
) {

    suspend operator fun invoke(): List<NativeWord> {
        return nativeWordRepository.getAllNativeWords()
    }
}