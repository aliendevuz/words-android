package uz.alien.dictup.domain.repository

interface SharedPrefsRepository {

    fun saveBoolean(key: String, value: Boolean)

    fun saveInt(key: String, value: Int)

    fun saveLong(key: String, value: Long)

    fun saveFloat(key: String, value: Float)

    fun saveString(key: String, value: String)


    fun getBoolean(key: String, default: Boolean): Boolean

    fun getInt(key: String, default: Int): Int

    fun getLong(key: String, default: Long): Long

    fun getFloat(key: String, default: Float): Float

    fun getString(key: String, default: String): String?
}