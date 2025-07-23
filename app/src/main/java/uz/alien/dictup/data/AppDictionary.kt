package uz.alien.dictup.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import uz.alien.dictup.domain.model.Story
import uz.alien.dictup.domain.model.Word
import java.io.File

object AppDictionary {

  private val TAG = AppDictionary::class.java.simpleName

  var beginnerUz: List<Word>? = emptyList()
  var beginnerEn: List<Word>? = emptyList()
  var beginnerStory: List<Story>? = emptyList()
  var essentialUz: List<Word>? = emptyList()
  var essentialEn: List<Word>? = emptyList()
  var essentialStory: List<Story>? = emptyList()

  fun initBeginnerUz(context: Context) {
    val file = File(context.filesDir, "src/beginner_uz.json")
    if (file.exists()) {
      beginnerUz = parseWordsFromJson<Word>(file.readText())
    } else {
      Log.d(TAG, "File not exist: ${file.absolutePath}")
    }
  }

  fun initBeginnerEn(context: Context) {
    val file = File(context.filesDir, "src/beginner_en.json")
    if (file.exists()) {
      beginnerEn = parseWordsFromJson<Word>(file.readText())
    } else {
      Log.d(TAG, "File not exist: ${file.absolutePath}")
    }
  }

  fun initBeginnerStory(context: Context) {
    val file = File(context.filesDir, "src/beginner_story.json")
    if (file.exists()) {
      beginnerStory = parseWordsFromJson<Story>(file.readText())
    } else {
      Log.d(TAG, "File not exist: ${file.absolutePath}")
    }
  }

  fun initEssentialUz(context: Context) {
    val file = File(context.filesDir, "src/essential_uz.json")
    if (file.exists()) {
      essentialUz = parseWordsFromJson<Word>(file.readText())
    } else {
      Log.d(TAG, "File not exist: ${file.absolutePath}")
    }
  }

  fun initEssentialEn(context: Context) {
    val file = File(context.filesDir, "src/essential_en.json")
    if (file.exists()) {
      essentialEn = parseWordsFromJson<Word>(file.readText())
    } else {
      Log.d(TAG, "File not exist: ${file.absolutePath}")
    }
  }

  fun initEssentialStory(context: Context) {
    val file = File(context.filesDir, "src/essential_story.json")
    if (file.exists()) {
      essentialStory = parseWordsFromJson<Story>(file.readText())
    } else {
      Log.d(TAG, "File not exist: ${file.absolutePath}")
    }
  }

  private inline fun <reified T> parseWordsFromJson(json: String): List<T> {
    val gson = Gson()
    val type = object : TypeToken<List<T>>() {}.type
    return gson.fromJson(json, type)
  }
}