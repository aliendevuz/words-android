package uz.alien.dictup.domain.usecase

import uz.alien.dictup.domain.model.Story
import uz.alien.dictup.domain.repository.room.StoryRepository

class GetStoriesByUnitUseCase(
    private val storyRepository: StoryRepository
) {

    suspend operator fun invoke(collectionId: Int, partId: Int, unitId: Int): List<Story> {
        return storyRepository.getStoriesByUnit(collectionId, partId, unitId)
    }
}