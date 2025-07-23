package uz.alien.test.payment

import android.content.Context
import androidx.core.content.edit

class PrefsManager private constructor(context: Context) {

  private val settings = "Settings"
  private val settingsPrefs = context.getSharedPreferences(settings, Context.MODE_PRIVATE)

  companion object {
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

  fun saveBool(key: String, value: Boolean) {
    settingsPrefs.edit { putBoolean(key, value) }
  }

  fun getBool(key: String, default: Boolean = false): Boolean {
    return settingsPrefs.getBoolean(key, default)
  }
}