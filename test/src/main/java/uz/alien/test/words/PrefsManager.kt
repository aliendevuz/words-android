package uz.alien.test.words

import android.content.Context
import androidx.core.content.edit

class PrefsManager private constructor(context: Context) {

  private val settings = "Settings"
  private val settingsPrefs = context.getSharedPreferences(settings, Context.MODE_PRIVATE)

  companion object {

    const val SRC_VERSION = "SRC_VERSION"

    @Volatile
    private var instance: PrefsManager? = null

    fun getInstance(context: Context): PrefsManager {
      return instance ?: synchronized(this) {
        instance ?: PrefsManager(context.applicationContext).also {
          instance = it
        }
      }
    }
  }

  fun saveString(key: String, value: String) {
    settingsPrefs.edit { putString(key, value) }
  }

  fun getString(key: String, default: String? = null): String? {
    return settingsPrefs.getString(key, default)
  }

  fun saveInt(key: String, value: Int) {
      settingsPrefs.edit { putInt(key, value) }
  }

  fun getInt(key: String, default: Int = 0): Int {
    return settingsPrefs.getInt(key, 0)
  }

  fun saveBool(key: String, value: Boolean) {
    settingsPrefs.edit { putBoolean(key, value) }
  }

  fun getBool(key: String, default: Boolean = false): Boolean {
    return settingsPrefs.getBoolean(key, default)
  }
}