package uz.alien.test.words

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object AppDictionary {
  var uzWords: List<Word> = emptyList()
  var enWords: List<Word> = emptyList()

  fun initialize(context: Context) {
    val uzString = File(context.filesDir, "words/uz.json").readText()
    val enString = File(context.filesDir, "words/en.json").readText()
    val uz = parseWordsFromJson(uzString)
    val en = parseWordsFromJson(enString)
    enWords = uz
    uzWords = en
  }

  private fun parseWordsFromJson(json: String): List<Word> {
    val gson = Gson()
    val type = object : TypeToken<List<Word>>() {}.type
    return gson.fromJson(json, type)
  }
}