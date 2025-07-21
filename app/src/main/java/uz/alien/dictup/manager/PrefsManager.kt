package uz.alien.dictup.manager

import android.content.Context

class PrefsManager private constructor(context: Context) {

  private val settings = "Settings"
  private val settingsPrefs = context.getSharedPreferences(settings, Context.MODE_PRIVATE)

  fun saveString(key: String, value: String) {
    settingsPrefs.edit().putString(key, value).apply()
  }

  fun getString(key: String, default: String? = null): String? {
    return settingsPrefs.getString(key, default)
  }

  fun saveBool(key: String, value: Boolean) {
    settingsPrefs.edit().putBoolean(key, value).apply()
  }

  fun getBool(key: String, default: Boolean = false): Boolean {
    return settingsPrefs.getBoolean(key, default)
  }

  fun saveInt(key: String, value: Int) {
    settingsPrefs.edit().putInt(key, value).apply()
  }

  fun getInt(key: String, default: Int = 0): Int {
    return settingsPrefs.getInt(key, default)
  }

  companion object {

    val BEGINNER_EN = "BEGINNER_EN"
    val BEGINNER_UZ = "BEGINNER_UZ"
    val BEGINNER_STORY = "BEGINNER_STORY"
    val ESSENTIAL_EN = "ESSENTIAL_EN"
    val ESSENTIAL_UZ = "ESSENTIAL_UZ"
    val ESSENTIAL_STORY = "ESSENTIAL_STORY"
    val IS_FIRST_TIME = "IS_FIRST_TIME"

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
}