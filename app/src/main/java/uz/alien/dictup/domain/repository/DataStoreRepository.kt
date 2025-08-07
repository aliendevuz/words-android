package uz.alien.dictup.domain.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {

    suspend fun saveUserName(name: String)

    fun getUserName(): Flow<String>

    suspend fun clearAll()
    suspend fun isLegacyDbMigrated(): Flow<Boolean>
    suspend fun setLegacyDbMigrated()
    suspend fun setFirstTimeOpening()
    suspend fun isFirstTimeOpening(): Flow<Boolean>

    suspend fun saveWordVersion(targetLang: String, collection: String, version: Double)
    suspend fun getWordVersion(targetLang: String, collection: String): Double
}