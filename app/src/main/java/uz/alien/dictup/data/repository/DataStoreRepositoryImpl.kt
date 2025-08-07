package uz.alien.dictup.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
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
        val IS_FIRST_TIME_OPENING = booleanPreferencesKey("is_first_time_opening")
        val CURRENT_USER_ID = intPreferencesKey("current_user_id")
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

    override suspend fun isLegacyDbMigrated(): Boolean {
        return context.dataStore.data.map { prefs ->
            prefs[IS_LEGACY_DB_MIGRATED] ?: false
        }.first()
    }

    override suspend fun setLegacyDbMigrated() {
        context.dataStore.edit { prefs ->
            prefs[IS_LEGACY_DB_MIGRATED] = true
        }
    }

    override suspend fun isFirstTimeOpening(): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[IS_FIRST_TIME_OPENING] ?: true
        }
    }

    override suspend fun setFirstTimeOpening() {
        context.dataStore.edit { prefs ->
            prefs[IS_FIRST_TIME_OPENING] = false
        }
    }

    override suspend fun saveWordVersion(targetLang: String, collection: String, version: Double) {
        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey("word.version.$targetLang.$collection")] = version.toString()
        }
    }

    override suspend fun getWordVersion(targetLang: String, collection: String): Double {
        return context.dataStore.data.map { prefs ->
            prefs[stringPreferencesKey("word.version.$targetLang.$collection")]?.toDoubleOrNull() ?: 0.0
        }.first()
    }

    override suspend fun saveStoryVersion(targetLang: String, collection: String, version: Double) {
        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey("story.version.$targetLang.$collection")] = version.toString()
        }
    }

    override suspend fun getStoryVersion(targetLang: String, collection: String): Double {
        return context.dataStore.data.map { prefs ->
            prefs[stringPreferencesKey("story.version.$targetLang.$collection")]?.toDoubleOrNull() ?: 0.0
        }.first()
    }

    override suspend fun saveNativeWordVersion(targetLang: String, collection: String, nativeLang: String, version: Double) {
        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey("native.word.version.$targetLang.$collection.$nativeLang")] = version.toString()
        }
    }

    override suspend fun getNativeWordVersion(targetLang: String, collection: String, nativeLang: String): Double {
        return context.dataStore.data.map { prefs ->
            prefs[stringPreferencesKey("native.word.version.$targetLang.$collection.$nativeLang")]?.toDoubleOrNull() ?: 0.0
        }.first()
    }

    override suspend fun saveNativeStoryVersion(targetLang: String, collection: String, nativeLang: String, version: Double) {
        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey("native.story.version.$targetLang.$collection.$nativeLang")] = version.toString()
        }
    }

    override suspend fun getNativeStoryVersion(targetLang: String, collection: String, nativeLang: String): Double {
        return context.dataStore.data.map { prefs ->
            prefs[stringPreferencesKey("native.story.version.$targetLang.$collection.$nativeLang")]?.toDoubleOrNull() ?: 0.0
        }.first()
    }

    override suspend fun saveCurrentUserId(userId: Int) {
        context.dataStore.edit { prefs ->
            prefs[CURRENT_USER_ID] = userId
        }
    }

    override suspend fun getCurrentUserId(): Int? {
        return context.dataStore.data.map { prefs ->
            prefs[CURRENT_USER_ID] ?: 0
        }.first()
    }

    override suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}