package uz.alien.dictup.domain.usecase.sync

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first
import uz.alien.dictup.domain.model.Word
import uz.alien.dictup.domain.repository.AssetsManagerRepository
import uz.alien.dictup.domain.repository.CacheManagerRepository
import uz.alien.dictup.domain.repository.DataStoreRepository
import uz.alien.dictup.domain.repository.room.WordRepository
import uz.alien.dictup.utils.Logger
import uz.alien.dictup.value.strings.DataStore.LOCAL_WORDS_VERSION
import uz.alien.dictup.value.strings.DataStore.WORDS_VERSION

class UpdateUseCase(
    private val wordRepository: WordRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val cacheManagerRepository: CacheManagerRepository,
    private val assetsManagerRepository: AssetsManagerRepository
) {

    private val gson = Gson()

    suspend operator fun invoke() {

        val wordsVersion = dataStoreRepository.getDouble("${WORDS_VERSION}en_essential").first()
        val localWordsVersion = dataStoreRepository.getDouble("${LOCAL_WORDS_VERSION}en_essential").first()

        if (wordsVersion != 0.0) {

            if (wordsVersion != localWordsVersion) {

                val words = getWords()

//                wordRepository.insertWords(words)

//                dataStoreRepository.saveDouble("${LOCAL_WORDS_VERSION}en_essential", wordsVersion)
            }
        } else {

            getWords()

            dataStoreRepository.saveDouble("${LOCAL_WORDS_VERSION}en_essential", 0.0)
        }


        Logger.d("$wordsVersion  $localWordsVersion")
    }

    suspend fun getWords(): List<Word> {

        val words = mutableListOf<Word>()

        Logger.d(assetsManagerRepository.loadBeginnerWords().subSequence(0, 200).toString())
        Logger.d(assetsManagerRepository.loadBeginnerStories().subSequence(0, 200).toString())
        Logger.d(assetsManagerRepository.loadBeginnerUzWords().subSequence(0, 200).toString())
        Logger.d(assetsManagerRepository.loadBeginnerUzStories().subSequence(0, 200).toString())
        Logger.d(assetsManagerRepository.loadEssentialWords().subSequence(0, 200).toString())
        Logger.d(assetsManagerRepository.loadEssentialStories().subSequence(0, 200).toString())
        Logger.d(assetsManagerRepository.loadEssentialUzWords().subSequence(0, 200).toString())
        Logger.d(assetsManagerRepository.loadEssentialUzStories().subSequence(0, 200).toString())

        val content = assetsManagerRepository.loadEssentialWords()

        val type = object : TypeToken<Map<Int, Map<Int, List<Word>>>>() {}.type

//        val wordsMap: Map<Int, Map<Int, List<Word>>> = gson.fromJson(content, type)

        return words
    }
}