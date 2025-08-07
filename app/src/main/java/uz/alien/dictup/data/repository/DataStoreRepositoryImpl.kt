package uz.alien.dictup.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
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
            prefs[stringPreferencesKey("$targetLang.$collection")] = version.toString()
        }
    }

    override suspend fun getWordVersion(targetLang: String, collection: String): Double {
        return context.dataStore.data.map { prefs ->
            prefs[stringPreferencesKey("$targetLang.$collection")]?.toDoubleOrNull() ?: 0.0
        }.first()
    }

    override suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}