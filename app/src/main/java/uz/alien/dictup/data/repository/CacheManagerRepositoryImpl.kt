package uz.alien.dictup.data.repository

import android.content.Context
import okio.IOException
import uz.alien.dictup.domain.repository.CacheManagerRepository
import uz.alien.dictup.utils.Logger
import uz.alien.dictup.value.strings.AssetsManager.BEGINNER_STORIES_FILENAME
import uz.alien.dictup.value.strings.AssetsManager.BEGINNER_STORIES_UZ_FILENAME
import uz.alien.dictup.value.strings.AssetsManager.BEGINNER_WORDS_FILENAME
import uz.alien.dictup.value.strings.AssetsManager.BEGINNER_WORDS_UZ_FILENAME
import uz.alien.dictup.value.strings.AssetsManager.ESSENTIAL_STORIES_FILENAME
import uz.alien.dictup.value.strings.AssetsManager.ESSENTIAL_STORIES_UZ_FILENAME
import uz.alien.dictup.value.strings.AssetsManager.ESSENTIAL_WORDS_FILENAME
import uz.alien.dictup.value.strings.AssetsManager.ESSENTIAL_WORDS_UZ_FILENAME
import java.io.File

class CacheManagerRepositoryImpl(
    private val context: Context
) : CacheManagerRepository {

    private fun saveContentToCache(fileName: String, json: String) {
        val file = File(context.cacheDir, fileName)
        file.writeText(json)
    }

    private fun loadContentFromCache(fileName: String): String {

        var content = ""
        val file = File(context.cacheDir, fileName)

        try {
            file.bufferedReader(Charsets.UTF_8).use { content = it.readText() }
        } catch (exception: IOException) {
            Logger.d("loadJSONFromAssets", "Exception loading JSON: ${exception.message}")
        }
        return content
    }

    private fun clearContent(fileName: String) {
        File(context.cacheDir, fileName).delete()
    }

    override fun saveBeginnerWords(content: String) {
        saveContentToCache(BEGINNER_WORDS_FILENAME, content)
    }

    override fun saveBeginnerStories(content: String) {
        saveContentToCache(BEGINNER_STORIES_FILENAME, content)
    }

    override fun saveBeginnerUzWords(content: String) {
        saveContentToCache(BEGINNER_WORDS_UZ_FILENAME, content)
    }

    override fun saveBeginnerUzStories(content: String) {
        saveContentToCache(BEGINNER_STORIES_UZ_FILENAME, content)
    }

    override fun loadBeginnerWords(): String {
        return loadContentFromCache(BEGINNER_WORDS_FILENAME)
    }

    override fun loadBeginnerStories(): String {
        return loadContentFromCache(BEGINNER_STORIES_FILENAME)
    }

    override fun loadBeginnerUzWords(): String {
        return loadContentFromCache(BEGINNER_WORDS_UZ_FILENAME)
    }

    override fun loadBeginnerUzStories(): String {
        return loadContentFromCache(BEGINNER_STORIES_UZ_FILENAME)
    }

    override fun clearBeginnerWords() {
        clearContent(BEGINNER_WORDS_FILENAME)
    }

    override fun clearBeginnerStories() {
        clearContent(BEGINNER_STORIES_FILENAME)
    }

    override fun clearBeginnerUzWords() {
        clearContent(BEGINNER_WORDS_UZ_FILENAME)
    }

    override fun clearBeginnerUzStories() {
        clearContent(BEGINNER_STORIES_UZ_FILENAME)
    }

    override fun saveEssentialWords(content: String) {
        saveContentToCache(ESSENTIAL_WORDS_FILENAME, content)
    }

    override fun saveEssentialStories(content: String) {
        saveContentToCache(ESSENTIAL_STORIES_FILENAME, content)
    }

    override fun saveEssentialUzWords(content: String) {
        saveContentToCache(ESSENTIAL_WORDS_UZ_FILENAME, content)
    }

    override fun saveEssentialUzStories(content: String) {
        saveContentToCache(ESSENTIAL_STORIES_UZ_FILENAME, content)
    }

    override fun loadEssentialWords(): String {
        return loadContentFromCache(ESSENTIAL_WORDS_FILENAME)
    }

    override fun loadEssentialStories(): String {
        return loadContentFromCache(ESSENTIAL_STORIES_FILENAME)
    }

    override fun loadEssentialUzWords(): String {
        return loadContentFromCache(ESSENTIAL_WORDS_UZ_FILENAME)
    }

    override fun loadEssentialUzStories(): String {
        return loadContentFromCache(ESSENTIAL_STORIES_UZ_FILENAME)
    }

    override fun clearEssentialWords() {
        clearContent(ESSENTIAL_WORDS_FILENAME)
    }

    override fun clearEssentialStories() {
        clearContent(ESSENTIAL_STORIES_FILENAME)
    }

    override fun clearEssentialUzWords() {
        clearContent(ESSENTIAL_WORDS_UZ_FILENAME)
    }

    override fun clearEssentialUzStories() {
        clearContent(ESSENTIAL_STORIES_UZ_FILENAME)
    }
}