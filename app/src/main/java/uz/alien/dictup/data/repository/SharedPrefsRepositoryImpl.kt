package uz.alien.dictup.data.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import uz.alien.dictup.domain.repository.SharedPrefsRepository
import uz.alien.dictup.value.strings.SharedPrefs.SHARED_PREFS
import kotlin.getValue
import kotlin.lazy

class SharedPrefsRepositoryImpl(private val context: Context) : SharedPrefsRepository {

    private val prefs by lazy {
        context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
    }

    override fun saveBoolean(key: String, value: Boolean) {
        prefs.edit {
            putBoolean(key, value)
            apply()
        }
    }

    override fun saveInt(key: String, value: Int) {
        prefs.edit {
            putInt(key, value)
            apply()
        }
    }

    override fun saveLong(key: String, value: Long) {
        prefs.edit {
            putLong(key, value)
            apply()
        }
    }

    override fun saveFloat(key: String, value: Float) {
        prefs.edit {
            putFloat(key, value)
            apply()
        }
    }

    override fun saveString(key: String, value: String) {
        prefs.edit {
            putString(key, value)
            apply()
        }
    }

    override fun getBoolean(key: String, default: Boolean): Boolean {
        return prefs.getBoolean(key, default)
    }

    override fun getInt(key: String, default: Int): Int {
        return prefs.getInt(key, default)
    }

    override fun getLong(key: String, default: Long): Long {
        return prefs.getLong(key, default)
    }

    override fun getFloat(key: String, default: Float): Float {
        return prefs.getFloat(key, default)
    }

    override fun getString(key: String, default: String): String? {
        return prefs.getString(key, default)
    }
}