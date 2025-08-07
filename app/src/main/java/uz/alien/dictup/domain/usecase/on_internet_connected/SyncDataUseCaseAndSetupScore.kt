package uz.alien.dictup.domain.usecase.on_internet_connected

import android.content.Context
import uz.alien.dictup.core.utils.Logger
import uz.alien.dictup.data.local.legacy.LegacyDatabase
import uz.alien.dictup.domain.model.NativeStory
import uz.alien.dictup.domain.model.NativeWord
import uz.alien.dictup.domain.model.Score
import uz.alien.dictup.domain.model.Story
import uz.alien.dictup.domain.model.Word
import uz.alien.dictup.domain.repository.DataStoreRepository
import uz.alien.dictup.domain.repository.retrofit.RemoteNativeStoryRepository
import uz.alien.dictup.domain.repository.retrofit.RemoteNativeWordRepository
import uz.alien.dictup.domain.repository.retrofit.RemoteStoryRepository
import uz.alien.dictup.domain.repository.retrofit.RemoteWordRepository
import uz.alien.dictup.domain.repository.room.NativeStoryRepository
import uz.alien.dictup.domain.repository.room.NativeWordRepository
import uz.alien.dictup.domain.repository.room.ScoreRepository
import uz.alien.dictup.domain.repository.room.StoryRepository
import uz.alien.dictup.domain.repository.room.WordRepository
import uz.alien.dictup.shared.WordCollection

class SyncDataUseCaseAndSetupScore(
    private val context: Context,
    private val remoteWordRepository: RemoteWordRepository,
    private val remoteStoryRepository: RemoteStoryRepository,
    private val remoteNativeWordRepository: RemoteNativeWordRepository,
    private val remoteNativeStoryRepository: RemoteNativeStoryRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val wordRepository: WordRepository,
    private val storyRepository: StoryRepository,
    private val nativeWordRepository: NativeWordRepository,
    private val nativeStoryRepository: NativeStoryRepository,
    private val scoreRepository: ScoreRepository
) {

    private val targetLang = "en"
    private val collections = listOf(
        WordCollection.BEGINNER,
        WordCollection.ESSENTIAL
    )
    private val nativeLang = "uz"

    private var wordId = 0
    private var storyId = 0
    private var nativeWordId = 0
    private var nativeStoryId = 0

    suspend operator fun invoke() {
        try {

            for (collection in collections) {

                wordSync(collection.key)

                nativeWordSync(collection.key)

                storySync(collection.key)

                nativeStorySync(collection.key)

                setupScore()

                if (collection == WordCollection.ESSENTIAL) {

                    migrate()
                }
            }

        } catch (e: retrofit2.HttpException) {
            Logger.d("HTTP Exception: ${e.response().toString()}")
        } catch (e: Exception) {
            Logger.d("Unexpected error: ${e.localizedMessage}")
        }
    }

    private suspend fun wordSync(collection: String) {

        val version = remoteWordRepository.getWordVersion(targetLang, collection)
        val localVersion = dataStoreRepository.getWordVersion(targetLang, collection)

        if (localVersion < version) {

            val remoteWords = remoteWordRepository.fetchWord(targetLang, collection)

            val collectionId = WordCollection.fromKey(collection)?.id
                ?: throw IllegalArgumentException("Unknown collection: $collection")

            val wordEntities = mutableListOf<Word>()

            for ((partId, unitMap) in remoteWords) {
                for ((unitId, wordList) in unitMap) {
                    wordList.forEach { word ->
                        wordId++
                        wordEntities.add(
                            word.copy(id = wordId, collectionId = collectionId, partId = partId, unitId = unitId)
                        )
                    }
                }
            }

            wordRepository.insertWords(wordEntities)
            dataStoreRepository.saveWordVersion(targetLang, collection, version)

            Logger.d("Inserted ${wordEntities.size} words for $collection")

        } else {
            Logger.d("No update needed for $collection")
        }
    }

    private suspend fun storySync(collection: String) {

        val version = remoteStoryRepository.getStoryVersion(targetLang, collection)
        val localVersion = dataStoreRepository.getStoryVersion(targetLang, collection)

        if (localVersion < version) {

            val remoteStories = remoteStoryRepository.fetchStory(targetLang, collection)

            val collectionId = WordCollection.fromKey(collection)?.id
                ?: throw IllegalArgumentException("Unknown collection: $collection")

            val storyEntities = mutableListOf<Story>()

            for ((partId, unitMap) in remoteStories) {
                for ((unitId, storyList) in unitMap) {
                    for ((_, story) in storyList) {
                        this.storyId++
                        storyEntities.add(
                            story.copy(id = this.storyId, collectionId = collectionId, partId = partId, unitId = unitId)
                        )
                    }
                }
            }

            storyRepository.insertStories(storyEntities)
            dataStoreRepository.saveStoryVersion(targetLang, collection, version)

            Logger.d("Inserted ${storyEntities.size} stories for $collection")

        } else {
            Logger.d("No update needed for $collection")
        }
    }

    private suspend fun nativeWordSync(collection: String) {

        val version = remoteNativeWordRepository.getNativeWordVersion(targetLang, collection, nativeLang)
        val localVersion = dataStoreRepository.getNativeWordVersion(targetLang, collection, nativeLang)

        if (localVersion < version) {

            val remoteNativeWords = remoteNativeWordRepository.fetchNativeWord(targetLang, collection, nativeLang)

            val nativeWordEntities = mutableListOf<NativeWord>()

            for ((_, unitMap) in remoteNativeWords) {
                for ((_, wordList) in unitMap) {
                    wordList.forEach { nativeWord ->
                        nativeWordId++
                        nativeWordEntities.add(
                            // ayni damda u single native languageni qo'llab quvvatlagani uchun
                            // bevosita wordId bilan nativeWordId o'zaro teng bo'lgani
                            // uchun shunday kiritib ketilgan
                            nativeWord.copy(id = nativeWordId, wordId = nativeWordId)
                        )
                    }
                }
            }

            nativeWordRepository.insertNativeWords(nativeWordEntities)
            dataStoreRepository.saveNativeWordVersion(targetLang, collection, nativeLang, version)

            Logger.d("Inserted ${nativeWordEntities.size} native words for $collection")

        } else {
            Logger.d("No update needed for $collection")
        }
    }

    private suspend fun nativeStorySync(collection: String) {

        val version = remoteNativeStoryRepository.getNativeStoryVersion(targetLang, collection, nativeLang)
        val localVersion = dataStoreRepository.getNativeStoryVersion(targetLang, collection, nativeLang)

        if (localVersion < version) {

            val remoteNativeStories = remoteNativeStoryRepository.fetchNativeStory(targetLang, collection, nativeLang)

            val nativeStoryEntities = mutableListOf<NativeStory>()

            for ((_, unitMap) in remoteNativeStories) {
                for ((_, storyList) in unitMap) {
                    for ((_, nativeStory) in storyList) {
                        nativeStoryId++
                        nativeStoryEntities.add(
                            // ayni damda u single native languageni qo'llab quvvatlagani uchun
                            // bevosita wordId bilan nativeWordId o'zaro teng bo'lgani
                            // uchun shunday kiritib ketilgan
                            nativeStory.copy(id = nativeStoryId, storyId = nativeStoryId)
                        )
                    }
                }
            }

            nativeStoryRepository.insertNativeStories(nativeStoryEntities)
            dataStoreRepository.saveNativeStoryVersion(targetLang, collection, nativeLang, version)

            Logger.d("Inserted ${nativeStoryEntities.size} native stories for $collection")

        } else {
            Logger.d("No update needed for $collection")
        }
    }

    private suspend fun setupScore() {

        val currentUserId = dataStoreRepository.getCurrentUserId()

        if (currentUserId != null) {

            val nativeWords = nativeWordRepository.getAllNativeWords()
            val scores = scoreRepository.getAllScores()

            if (nativeWords.size != scores.size) {

                val scores = mutableListOf<Score>()

                for (nativeWord in nativeWords) {
                    // bu yerda Score NativeWord id ga asoslanadiva ularni o'zaro id si bir xil
                    scores.add(Score(id = nativeWord.id!!, userId = currentUserId, wordId = nativeWord.id))
                }

                scoreRepository.insertScores(scores)

                Logger.d("Inserted ${scores.size} scores")
            }
        } else {
            Logger.w("User not found")
        }
    }

    private suspend fun migrate() {

        val isMigrated = dataStoreRepository.isLegacyDbMigrated()

        if (isMigrated) {
            Logger.d("Legacy Database already migrated!")
            return
        }

        val legacyDatabase = LegacyDatabase.getInstance(context)
        val legacyDao = legacyDatabase.wordDao()
        val legacyWords = legacyDao.getWords()

        legacyWords.forEach { word ->

            val targetId = word.id + 601
            val score = scoreRepository.getScoreById(targetId) ?: return@forEach

            when {
                word.level > 0 -> {
                    scoreRepository.updateScore(score.copy(correctCount = word.level))
                }
                word.level < 0 -> {
                    scoreRepository.updateScore(score.copy(incorrectCount = word.level))
                }
            }
        }

        legacyDao.clear()

        LegacyDatabase.destroyInstance()

        Logger.d("Legacy Database migrated!")
    }
}