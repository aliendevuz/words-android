package uz.alien.dictup.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import uz.alien.dictup.data.local.store.dataStore
import uz.alien.dictup.domain.repository.DataStoreRepository

class DataStoreRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : DataStoreRepository {

    companion object {
        val USERNAME_KEY = stringPreferencesKey("username")
        val IS_LEGACY_DB_MIGRATED = booleanPreferencesKey("is_legacy_db_migrated")
        val CURRENT_USER_ID = intPreferencesKey("current_user_id")
        val LAST_SYNC_TIME = stringPreferencesKey("last_sync_time")
        val IS_SYNC_COMPLETED = booleanPreferencesKey("is_sync_completed")
    }

    override suspend fun saveUserName(name: String) {
        context.dataStore.edit { prefs ->
            prefs[USERNAME_KEY] = name
        }
    }

    override fun getUserName(): Flow<String> {
        return context.dataStore.data.map { prefs ->
            prefs[USERNAME_KEY] ?: ""
        }
    }

    override suspend fun isLegacyDbMigrated(): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[IS_LEGACY_DB_MIGRATED] ?: false
        }
    }

    override suspend fun setLegacyDbMigrated() {
        context.dataStore.edit { prefs ->
            prefs[IS_LEGACY_DB_MIGRATED] = true
        }
    }

    override suspend fun saveWordVersion(targetLang: String, collection: String, version: Double) {
        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey("word.version.$targetLang.$collection")] = version.toString()
        }
    }

    override suspend fun getWordVersion(targetLang: String, collection: String): Flow<Double> {
        return context.dataStore.data.map { prefs ->
            prefs[stringPreferencesKey("word.version.$targetLang.$collection")]?.toDoubleOrNull() ?: 0.0
        }
    }

    override suspend fun saveStoryVersion(targetLang: String, collection: String, version: Double) {
        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey("story.version.$targetLang.$collection")] = version.toString()
        }
    }

    override suspend fun getStoryVersion(targetLang: String, collection: String): Flow<Double> {
        return context.dataStore.data.map { prefs ->
            prefs[stringPreferencesKey("story.version.$targetLang.$collection")]?.toDoubleOrNull() ?: 0.0
        }
    }

    override suspend fun saveNativeWordVersion(targetLang: String, collection: String, nativeLang: String, version: Double) {
        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey("native.word.version.$targetLang.$collection.$nativeLang")] = version.toString()
        }
    }

    override suspend fun getNativeWordVersion(targetLang: String, collection: String, nativeLang: String): Flow<Double> {
        return context.dataStore.data.map { prefs ->
            prefs[stringPreferencesKey("native.word.version.$targetLang.$collection.$nativeLang")]?.toDoubleOrNull() ?: 0.0
        }
    }

    override suspend fun saveNativeStoryVersion(targetLang: String, collection: String, nativeLang: String, version: Double) {
        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey("native.story.version.$targetLang.$collection.$nativeLang")] = version.toString()
        }
    }

    override suspend fun getNativeStoryVersion(targetLang: String, collection: String, nativeLang: String): Flow<Double> {
        return context.dataStore.data.map { prefs ->
            prefs[stringPreferencesKey("native.story.version.$targetLang.$collection.$nativeLang")]?.toDoubleOrNull() ?: 0.0
        }
    }

    override suspend fun saveCurrentUserId(userId: Int) {
        context.dataStore.edit { prefs ->
            prefs[CURRENT_USER_ID] = userId
        }
    }

    override suspend fun getCurrentUserId(): Flow<Int?> {
        return context.dataStore.data.map { prefs ->
            prefs[CURRENT_USER_ID]
        }
    }

    override suspend fun saveLastSyncTime(time: Long) {
        context.dataStore.edit { prefs ->
            prefs[LAST_SYNC_TIME] = time.toString()
        }
    }

    override suspend fun getLastSyncTime(): Flow<Long?> {
        return context.dataStore.data.map { prefs ->
            prefs[LAST_SYNC_TIME]?.toLongOrNull()
        }
    }

    override suspend fun syncCompleted() {
        context.dataStore.edit { prefs ->
            prefs[IS_SYNC_COMPLETED] = true
        }
    }

    override suspend fun isSyncCompleted(): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[IS_SYNC_COMPLETED] ?: false
        }.distinctUntilChanged()
    }

    override suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}