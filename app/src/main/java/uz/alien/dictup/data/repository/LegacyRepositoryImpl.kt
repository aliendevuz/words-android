package uz.alien.dictup.data.repository

import uz.alien.dictup.data.local.legacy.LegacyDao
import uz.alien.dictup.domain.repository.LegacyRepository

class LegacyRepositoryImpl(
    private val legacyDao: LegacyDao
): LegacyRepository {

    override suspend fun getLegacyWords() = legacyDao.getWords()

    override suspend fun getLegacyScores() = legacyDao.getLevels()

    override suspend fun clearLegacyWords() = legacyDao.clear()
}