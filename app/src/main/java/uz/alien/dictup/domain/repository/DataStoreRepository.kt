package uz.alien.dictup.domain.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {

    suspend fun saveUserName(name: String)

    fun getUserName(): Flow<String>

    suspend fun clearAll()
    suspend fun isLegacyDbMigrated(): Boolean
    suspend fun setLegacyDbMigrated()
    suspend fun setFirstTimeOpening()
    suspend fun isFirstTimeOpening(): Flow<Boolean>

    suspend fun saveWordVersion(targetLang: String, collection: String, version: Double)
    suspend fun getWordVersion(targetLang: String, collection: String): Double

    suspend fun saveStoryVersion(targetLang: String, collection: String, version: Double)
    suspend fun getStoryVersion(targetLang: String, collection: String): Double

    suspend fun saveNativeWordVersion(targetLang: String, collection: String, nativeLang: String, version: Double)
    suspend fun getNativeWordVersion(targetLang: String, collection: String, nativeLang: String): Double

    suspend fun saveNativeStoryVersion(targetLang: String, collection: String, nativeLang: String, version: Double)
    suspend fun getNativeStoryVersion(targetLang: String, collection: String, nativeLang: String): Double

    suspend fun saveCurrentUserId(userId: Int)
    suspend fun getCurrentUserId(): Int?
}