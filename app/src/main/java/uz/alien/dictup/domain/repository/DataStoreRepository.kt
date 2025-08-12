package uz.alien.dictup.domain.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {

    suspend fun saveUserName(name: String)

    fun getUserName(): Flow<String>

    suspend fun clearAll()
    suspend fun isLegacyDbMigrated(): Flow<Boolean>
    suspend fun setLegacyDbMigrated()

    suspend fun saveWordVersion(targetLang: String, collection: String, version: Double)
    suspend fun getWordVersion(targetLang: String, collection: String): Flow<Double>

    suspend fun saveStoryVersion(targetLang: String, collection: String, version: Double)
    suspend fun getStoryVersion(targetLang: String, collection: String): Flow<Double>

    suspend fun saveNativeWordVersion(targetLang: String, collection: String, nativeLang: String, version: Double)
    suspend fun getNativeWordVersion(targetLang: String, collection: String, nativeLang: String): Flow<Double>

    suspend fun saveNativeStoryVersion(targetLang: String, collection: String, nativeLang: String, version: Double)
    suspend fun getNativeStoryVersion(targetLang: String, collection: String, nativeLang: String): Flow<Double>

    suspend fun saveCurrentUserId(userId: Int)
    suspend fun getCurrentUserId(): Flow<Int?>

    suspend fun saveLastSyncTime(time: Long)

    suspend fun getLastSyncTime(): Flow<Long?>

    suspend fun syncCompleted()

    suspend fun isSyncCompleted(): Flow<Boolean>
}