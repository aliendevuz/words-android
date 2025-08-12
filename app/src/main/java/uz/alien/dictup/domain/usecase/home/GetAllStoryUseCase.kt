package uz.alien.dictup.domain.usecase.home

import uz.alien.dictup.domain.model.Story
import uz.alien.dictup.domain.repository.room.StoryRepository

class GetAllStoryUseCase(
    private val storyRepository: StoryRepository
) {

    suspend operator fun invoke(): List<Story> {
        return storyRepository.getAllStories()
    }
}