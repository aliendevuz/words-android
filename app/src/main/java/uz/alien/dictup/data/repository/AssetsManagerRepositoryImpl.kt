package uz.alien.dictup.data.repository

import android.content.Context
import okio.IOException
import uz.alien.dictup.domain.repository.AssetsManagerRepository
import uz.alien.dictup.utils.Logger
import uz.alien.dictup.value.strings.AssetsManager.BEGINNER_STORIES_FILENAME
import uz.alien.dictup.value.strings.AssetsManager.BEGINNER_STORIES_UZ_FILENAME
import uz.alien.dictup.value.strings.AssetsManager.BEGINNER_WORDS_FILENAME
import uz.alien.dictup.value.strings.AssetsManager.BEGINNER_WORDS_UZ_FILENAME
import uz.alien.dictup.value.strings.AssetsManager.ESSENTIAL_STORIES_FILENAME
import uz.alien.dictup.value.strings.AssetsManager.ESSENTIAL_STORIES_UZ_FILENAME
import uz.alien.dictup.value.strings.AssetsManager.ESSENTIAL_WORDS_FILENAME
import uz.alien.dictup.value.strings.AssetsManager.ESSENTIAL_WORDS_UZ_FILENAME

class AssetsManagerRepositoryImpl(
    val context: Context
) : AssetsManagerRepository {

    fun loadJSONFromAssets(fileName: String): String {

        var content = ""
        val file = context.assets.open(fileName)

        try {
            file.bufferedReader(Charsets.UTF_8).use { content = it.readText() }
        } catch (exception: IOException) {
            Logger.d("loadJSONFromAssets", "Exception loading JSON: ${exception.message}")
        }
        return content
    }

    override fun loadBeginnerWords(): String {

        return loadJSONFromAssets(BEGINNER_WORDS_FILENAME)
    }

    override fun loadBeginnerStories(): String {

        return loadJSONFromAssets(BEGINNER_STORIES_FILENAME)
    }

    override fun loadBeginnerUzWords(): String {

        return loadJSONFromAssets(BEGINNER_WORDS_UZ_FILENAME)
    }

    override fun loadBeginnerUzStories(): String {

        return loadJSONFromAssets(BEGINNER_STORIES_UZ_FILENAME)
    }

    override fun loadEssentialWords(): String {

        return loadJSONFromAssets(ESSENTIAL_WORDS_FILENAME)
    }

    override fun loadEssentialStories(): String {

        return loadJSONFromAssets(ESSENTIAL_STORIES_FILENAME)
    }

    override fun loadEssentialUzWords(): String {

        return loadJSONFromAssets(ESSENTIAL_WORDS_UZ_FILENAME)
    }

    override fun loadEssentialUzStories(): String {

        return loadJSONFromAssets(ESSENTIAL_STORIES_UZ_FILENAME)
    }
}