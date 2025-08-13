package uz.alien.dictup.domain.usecase

import android.content.Context
import kotlinx.coroutines.flow.firstOrNull
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
import kotlin.collections.iterator

class SyncDataUseCase(
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

    enum class SyncStep {
        WORDS_BEGINNER,
        NATIVE_WORDS_BEGINNER,
        STORIES_BEGINNER,
        NATIVE_STORIES_BEGINNER,
        WORDS_ESSENTIAL,
        NATIVE_WORDS_ESSENTIAL,
        STORIES_ESSENTIAL,
        NATIVE_STORIES_ESSENTIAL,
        SETUP_SCORE,
        MIGRATE
    }

    private val targetLang = "en"
    private val nativeLang = "uz"

    private var wordId = 0
    private var storyId = 0
    private var nativeWordId = 0
    private var nativeStoryId = 0

    suspend fun runAllSteps() {

        val lastSyncTime = dataStoreRepository.getLastSyncTime().firstOrNull()

        if (lastSyncTime == null) {
            begin()
        } else {

            val currentTime = System.currentTimeMillis()
            val hour = 1000 * 60 * 60

            if (lastSyncTime + hour > currentTime) {
                begin()
            } else {
                Logger.d(SyncDataUseCase::class.java.simpleName, "No update needed $lastSyncTime")
            }
        }
    }

    private suspend fun begin() {

        val steps = listOf(
            SyncStep.WORDS_BEGINNER,
            SyncStep.NATIVE_WORDS_BEGINNER,
            SyncStep.STORIES_BEGINNER,
            SyncStep.NATIVE_STORIES_BEGINNER,
            SyncStep.WORDS_ESSENTIAL,
            SyncStep.NATIVE_WORDS_ESSENTIAL,
            SyncStep.STORIES_ESSENTIAL,
            SyncStep.NATIVE_STORIES_ESSENTIAL,
            SyncStep.SETUP_SCORE,
            SyncStep.MIGRATE
        )

        for (step in steps) {
            runStep(step)
        }

        dataStoreRepository.saveLastSyncTime(System.currentTimeMillis())
        dataStoreRepository.syncCompleted()

        Logger.d(SyncDataUseCase::class.java.simpleName, "Sync completed")
    }

    private suspend fun runStep(syncStep: SyncStep) {

        when (syncStep) {

            SyncStep.WORDS_BEGINNER -> wordSync("beginner")
            SyncStep.NATIVE_WORDS_BEGINNER -> nativeWordSync("beginner")
            SyncStep.STORIES_BEGINNER -> storySync("beginner")
            SyncStep.NATIVE_STORIES_BEGINNER -> nativeStorySync("beginner")
            SyncStep.WORDS_ESSENTIAL -> wordSync("essential")
            SyncStep.NATIVE_WORDS_ESSENTIAL -> nativeWordSync("essential")
            SyncStep.STORIES_ESSENTIAL -> storySync("essential")
            SyncStep.NATIVE_STORIES_ESSENTIAL -> nativeStorySync("essential")
            SyncStep.SETUP_SCORE -> setupScore()
            SyncStep.MIGRATE -> migrate()
        }
    }

    private suspend fun wordSync(collection: String) {

        val localVersion = dataStoreRepository.getWordVersion(targetLang, collection).firstOrNull() ?: 0.0

        val version = remoteWordRepository.getWordVersion(targetLang, collection)

        if (localVersion < version) {

            val remoteWords = remoteWordRepository.fetchWord(targetLang, collection)

            val collectionId = WordCollection.Companion.fromKey(collection)?.id
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

            Logger.d(SyncDataUseCase::class.java.simpleName, "Inserted ${wordEntities.size} words for $collection")

        } else {
            Logger.d(SyncDataUseCase::class.java.simpleName, "No update needed for $collection")
        }
    }

    private suspend fun storySync(collection: String) {

        val localVersion = dataStoreRepository.getStoryVersion(targetLang, collection).firstOrNull() ?: 0.0

        val version = remoteStoryRepository.getStoryVersion(targetLang, collection)

        if (localVersion < version) {

            val remoteStories = remoteStoryRepository.fetchStory(targetLang, collection)

            val collectionId = WordCollection.Companion.fromKey(collection)?.id
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

            Logger.d(SyncDataUseCase::class.java.simpleName, "Inserted ${storyEntities.size} stories for $collection")

        } else {
            Logger.d(SyncDataUseCase::class.java.simpleName, "No update needed for $collection")
        }
    }

    private suspend fun nativeWordSync(collection: String) {

        val localVersion = dataStoreRepository.getNativeWordVersion(targetLang, collection, nativeLang).firstOrNull() ?: 0.0

        val version = remoteNativeWordRepository.getNativeWordVersion(targetLang, collection, nativeLang)

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

            Logger.d(SyncDataUseCase::class.java.simpleName, "Inserted ${nativeWordEntities.size} native words for $collection")

        } else {
            Logger.d(SyncDataUseCase::class.java.simpleName, "No update needed for $collection")
        }
    }

    private suspend fun nativeStorySync(collection: String) {

        val localVersion = dataStoreRepository.getNativeStoryVersion(targetLang, collection, nativeLang).firstOrNull() ?: 0.0

        val version = remoteNativeStoryRepository.getNativeStoryVersion(targetLang, collection, nativeLang)

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

            Logger.d(SyncDataUseCase::class.java.simpleName, "Inserted ${nativeStoryEntities.size} native stories for $collection")

        } else {
            Logger.d(SyncDataUseCase::class.java.simpleName, "No update needed for $collection")
        }
    }

    private suspend fun setupScore() {

        val currentUserId = dataStoreRepository.getCurrentUserId().firstOrNull()

        if (currentUserId != null) {

            val words = wordRepository.getAllWords()
            val scores = scoreRepository.getAllScores()

            if (words.size != scores.size) {

                val scores = mutableListOf<Score>()

                for (word in words) {
                    // bu yerda Score NativeWord id ga asoslanadiva ularni o'zaro id si bir xil
                    scores.add(Score(id = word.id!!, userId = currentUserId, wordId = word.id))
                }

                scoreRepository.insertScores(scores)

                Logger.d(SyncDataUseCase::class.java.simpleName, "Inserted ${scores.size} scores")
            }
        } else {
            Logger.d(SyncDataUseCase::class.java.simpleName, "User not found")
        }
    }

    private suspend fun migrate() {

        val isMigrated = dataStoreRepository.isLegacyDbMigrated().firstOrNull() ?: false

        Logger.d(SyncDataUseCase::class.java.simpleName, "Legacy Database already migrated!")
        if (isMigrated) return

        val legacyDatabase = LegacyDatabase.Companion.getInstance(context)
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

        dataStoreRepository.setLegacyDbMigrated()

        legacyDao.clear()

        LegacyDatabase.Companion.destroyInstance()

        Logger.d(SyncDataUseCase::class.java.simpleName, "Legacy Database migrated!")
    }
}