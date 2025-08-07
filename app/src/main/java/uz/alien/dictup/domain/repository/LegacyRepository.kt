package uz.alien.dictup.domain.repository

import uz.alien.dictup.data.local.legacy.LegacyScore
import uz.alien.dictup.data.local.legacy.Word

interface LegacyRepository {

    suspend fun getLegacyWords(): List<Word>

    suspend fun getLegacyScores(): List<LegacyScore>

    suspend fun clearLegacyWords()
}