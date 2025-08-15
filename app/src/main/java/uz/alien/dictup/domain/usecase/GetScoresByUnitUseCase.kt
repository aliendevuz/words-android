package uz.alien.dictup.domain.usecase

import uz.alien.dictup.domain.model.Score
import uz.alien.dictup.domain.repository.room.ScoreRepository

class GetScoresByUnitUseCase(
    private val scoreRepository: ScoreRepository
) {

    suspend operator fun invoke(collectionId: Int, partId: Int, unitId: Int): List<Score> {
        return scoreRepository.getScoresByFullPath(collectionId, partId, unitId)
    }
}