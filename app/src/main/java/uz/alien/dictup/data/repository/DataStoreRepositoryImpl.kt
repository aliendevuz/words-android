package uz.alien.dictup.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import uz.alien.dictup.domain.repository.DataStoreRepository

class DataStoreRepositoryImpl (private val context: Context) : DataStoreRepository {

    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "data_store")

    override suspend fun saveBoolean(key: String, value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[booleanPreferencesKey(key)] = value
        }
    }

    override suspend fun getBoolean(key: String): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[booleanPreferencesKey(key)] ?: false
        }.distinctUntilChanged()
    }

    override suspend fun saveInt(key: String, value: Int) {
        context.dataStore.edit { prefs ->
            prefs[intPreferencesKey(key)] = value
        }
    }

    override suspend fun getInt(key: String): Flow<Int> {
        return context.dataStore.data.map { prefs ->
            prefs[intPreferencesKey(key)] ?: 0
        }.distinctUntilChanged()
    }

    override suspend fun saveLong(key: String, value: Long) {
        context.dataStore.edit { prefs ->
            prefs[longPreferencesKey(key)] = value
        }
    }

    override suspend fun getLong(key: String): Flow<Long> {
        return context.dataStore.data.map { prefs ->
            prefs[longPreferencesKey(key)] ?: 0L
        }.distinctUntilChanged()
    }

    override suspend fun saveFloat(key: String, value: Float) {
        context.dataStore.edit { prefs ->
            prefs[floatPreferencesKey(key)] = value
        }
    }

    override suspend fun getFloat(key: String): Flow<Float> {
        return context.dataStore.data.map { prefs ->
            prefs[floatPreferencesKey(key)] ?: 0f
        }.distinctUntilChanged()
    }

    override suspend fun saveDouble(key: String, value: Double) {
        context.dataStore.edit { prefs ->
            prefs[doublePreferencesKey(key)] = value
        }
    }

    override suspend fun getDouble(key: String): Flow<Double> {
        return context.dataStore.data.map { prefs ->
            prefs[doublePreferencesKey(key)] ?: 0.0
        }.distinctUntilChanged()
    }

    override suspend fun saveString(key: String, value: String) {
        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey(key)] = value
        }
    }

    override suspend fun getString(key: String): Flow<String> {
        return context.dataStore.data.map { prefs ->
            prefs[stringPreferencesKey(key)] ?: ""
        }.distinctUntilChanged()
    }
}