package uz.alien.dictup.domain.usecase.main

import uz.alien.dictup.domain.model.NativeStory
import uz.alien.dictup.domain.repository.room.NativeStoryRepository

class GetAllNativeStoryUseCase(
    private val nativeStoryRepository: NativeStoryRepository
) {

    suspend operator fun invoke(): List<NativeStory> {
        return nativeStoryRepository.getAllNativeStories()
    }
}