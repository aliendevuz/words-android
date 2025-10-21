package uz.alien.dictup.domain.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {

    suspend fun saveBoolean(key: String, value: Boolean)

    suspend fun getBoolean(key: String, default: Boolean = false): Flow<Boolean>

    suspend fun saveInt(key: String, value: Int)

    suspend fun getInt(key: String, default: Int = 0): Flow<Int>

    suspend fun saveLong(key: String, value: Long)

    suspend fun getLong(key: String): Flow<Long>

    suspend fun saveFloat(key: String, value: Float)

    suspend fun getFloat(key: String): Flow<Float>

    suspend fun saveDouble(key: String, value: Double)

    suspend fun getDouble(key: String): Flow<Double>

    suspend fun saveString(key: String, value: String)

    suspend fun getString(key: String): Flow<String>
}