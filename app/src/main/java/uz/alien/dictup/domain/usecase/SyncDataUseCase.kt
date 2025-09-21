package uz.alien.dictup.domain.usecase

import android.content.Context
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import uz.alien.dictup.utils.Logger
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
import uz.alien.dictup.domain.model.WordCollection
import uz.alien.dictup.domain.repository.CacheManagerRepository
import uz.alien.dictup.domain.repository.HttpManager
import uz.alien.dictup.domain.repository.SharedPrefsRepository
import uz.alien.dictup.value.strings.DataStore.IS_LEGACY_DB_MIGRATED
import uz.alien.dictup.value.strings.DataStore.IS_SYNC_COMPLETED
import uz.alien.dictup.value.strings.DataStore.LAST_SYNC_TIME
import uz.alien.dictup.value.strings.DataStore.NATIVE_STORY_VERSION
import uz.alien.dictup.value.strings.DataStore.NATIVE_WORD_VERSION
import uz.alien.dictup.value.strings.DataStore.STORY_VERSION
import uz.alien.dictup.value.strings.DataStore.WORDS_VERSION
import uz.alien.dictup.value.strings.SharedPrefs.IS_READY
import java.io.File
import java.security.MessageDigest
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
    private val scoreRepository: ScoreRepository,
    private val prefsRepository: SharedPrefsRepository,
    private val httpManager: HttpManager,
    private val cacheManagerRepository: CacheManagerRepository
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

    private var tryingCount = 0
    private var success = false

    private var step = 0

    suspend fun <T> retryRequest(
        times: Int = 3,
        initialDelay: Long = 1000L, // 1s
        maxDelay: Long = 8000L,     // 8s
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelay
        repeat(times - 1) { attempt ->
            try {
                return block()
            } catch (e: Exception) {
                Logger.d("Http", "Attempt ${attempt + 1} failed: ${e.message}")
            }
            delay(currentDelay)
            currentDelay = (currentDelay * 2).coerceAtMost(maxDelay)
        }
        return block() // oxirgi urinish
    }

    suspend fun runAllSteps() {

        val versionUrl = "https://assets.4000.uz/assets/.v/$targetLang/essential/words.json"
        val hashUrl = "https://assets.4000.uz/assets/.hash/$targetLang/essential/words.json.sha256"
        val fileUrl = "https://assets.4000.uz/assets/$targetLang/essential/words.json"

        val wordVersion = remoteWordRepository.getWordVersion("en", "essential")

        val versionResult = retryRequest { httpManager.get(versionUrl) }
        var remoteVersion = 0.0

        versionResult.fold(onSuccess = {
            remoteVersion = it.trim().toDouble()
            Logger.d("Remote version: $remoteVersion")
        }, onFailure = {
            Logger.d("Version fetch error: ${it.message}")
            return
        })

        // 1. Hashni olish
        val hashResult = retryRequest { httpManager.get(hashUrl) }
        var expectedHash = ""
        hashResult.fold(onSuccess = {
            expectedHash = it.trim()
            Logger.d("Remote hash: $expectedHash")
        }, onFailure = {
            Logger.d("Hash fetch error: ${it.message}")
            return
        })

        // 2. Faylni olish
        val fileResult = retryRequest { httpManager.getBytes(fileUrl) }
        fileResult.fold(onSuccess = { bytes ->

            val success = bytes.calculateHash(expectedHash)

            if (success) {

                val file = File(context.cacheDir, "words.json")
                file.writeBytes(bytes)

                dataStoreRepository.saveDouble("${WORDS_VERSION}_en_essential", wordVersion)

                Logger.d("File fetched successfully")
            } else {

                Logger.d("File is damaged")
            }
        }, onFailure = {
            Logger.d("File fetch error: ${it.message}")
            return
        })

//        val lastSyncTime = dataStoreRepository.getLong(LAST_SYNC_TIME).firstOrNull()
//
//        if (lastSyncTime == null) {
//            begin()
//        } else {
//
//            val currentTime = System.currentTimeMillis()
//            val hour = 1000 * 60 * 60
//
//            if (lastSyncTime + hour < currentTime) {
//                begin()
//            } else {
//                Logger.d(SyncDataUseCase::class.java.simpleName, "No update needed $lastSyncTime")
//            }
//        }
    }

    fun ByteArray.calculateHash(expectedHash: String): Boolean {
        val digest = MessageDigest.getInstance("SHA-256")
        val actualHash = digest.digest(this).joinToString("") { "%02x".format(it) }
        return actualHash.equals(expectedHash.trim(), ignoreCase = true)
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

        while (step < steps.size) {
            runStep(steps[step])
        }

        dataStoreRepository.saveLong(LAST_SYNC_TIME, System.currentTimeMillis())
        dataStoreRepository.saveBoolean(IS_SYNC_COMPLETED, true)
        prefsRepository.saveBoolean(IS_READY, true)

        Logger.d(SyncDataUseCase::class.java.simpleName, "Sync completed")
    }

    private suspend fun runStep(syncStep: SyncStep) {

        tryingCount = 0
        success = false

        when (syncStep) {

            SyncStep.WORDS_BEGINNER -> {

                while (!success && tryingCount < 3) {
                    success = wordSync("beginner", 20 * 20 * 4)
                    tryingCount++
                }

                if (!success) {
                    Logger.e("SyncDataUseCase", "WORD_BEGINNER sync failed after $tryingCount attempts")
                    clear()
                    step = 0
                    return
                }
            }
            SyncStep.NATIVE_WORDS_BEGINNER -> {

                while (!success && tryingCount < 3) {
                    success = nativeWordSync("beginner", 20 * 20 * 4)
                    tryingCount++
                }

                if (!success) {
                    Logger.e(
                        "SyncDataUseCase",
                        "NATIVE_WORD_BEGINNER sync failed after $tryingCount attempts"
                    )
                    clear()
                    step = 0
                    return
                }
            }
            SyncStep.STORIES_BEGINNER -> {

                while (!success && tryingCount < 3) {
                    success = storySync("beginner", 3 * 20 * 4)
                    tryingCount++
                }

                if (!success) {
                    Logger.e("SyncDataUseCase", "STORY_BEGINNER sync failed after $tryingCount attempts")
                    clear()
                    step = 0
                    return
                }
            }
            SyncStep.NATIVE_STORIES_BEGINNER -> {

                while (!success && tryingCount < 3) {
                    success = nativeStorySync("beginner", 3 * 20 * 4)
                    tryingCount++
                }

                if (!success) {
                    Logger.e(
                        "SyncDataUseCase",
                        "NATIVE_STORY_BEGINNER sync failed after $tryingCount attempts"
                    )
                    clear()
                    step = 0
                    return
                }
            }
            SyncStep.WORDS_ESSENTIAL -> {

                while (!success && tryingCount < 3) {
                    success = wordSync("essential", 20 * 30 * 6)
                    tryingCount++
                }

                if (!success) {
                    Logger.e("SyncDataUseCase", "WORD_ESSENTIAL sync failed after $tryingCount attempts")
                    clear()
                    step = 0
                    return
                }
            }
            SyncStep.NATIVE_WORDS_ESSENTIAL -> {

                while (!success && tryingCount < 3) {
                    success = nativeWordSync("essential", 20 * 30 * 6)
                    tryingCount++
                }

                if (!success) {
                    Logger.e(
                        "SyncDataUseCase",
                        "NATIVE_WORD_ESSENTIAL sync failed after $tryingCount attempts"
                    )
                    clear()
                    step = 0
                    return
                }
            }
            SyncStep.STORIES_ESSENTIAL -> {

                while (!success && tryingCount < 3) {
                    success = storySync("essential", 3 * 30 * 6)
                    tryingCount++
                }

                if (!success) {
                    Logger.e("SyncDataUseCase", "STORY_ESSENTIAL sync failed after $tryingCount attempts")
                    clear()
                    step = 0
                    return
                }
            }
            SyncStep.NATIVE_STORIES_ESSENTIAL -> {

                while (!success && tryingCount < 3) {
                    success = nativeStorySync("essential", 3 * 30 * 6)
                    tryingCount++
                }

                if (!success) {
                    Logger.e(
                        "SyncDataUseCase",
                        "NATIVE_STORY_ESSENTIAL sync failed after $tryingCount attempts"
                    )
                    clear()
                    step = 0
                    return
                }
            }
            SyncStep.SETUP_SCORE -> {

                while (!success && tryingCount < 3) {
                    success = setupScore(20 * 20 * 4 + 20 * 30 * 6)
                    tryingCount++
                }

                if (!success) {
                    Logger.e("SyncDataUseCase", "SETUP_SCORE sync failed after $tryingCount attempts")
                    clear()
                    step = 0
                    return
                }
            }
            SyncStep.MIGRATE -> {

                while (!success && tryingCount < 3) {
                    success = migrate(20 * 30 * 6)
                    tryingCount++
                }

                if (!success) {
                    Logger.e("SyncDataUseCase", "MIGRATE sync failed after $tryingCount attempts")
                    clear()
                    step = 0
                    return
                }
            }
        }
        step++
    }

    private suspend fun clear() {
        wordRepository.clearAllWords()
        storyRepository.clearAllStories()
        nativeWordRepository.clearAllNativeWords()
        nativeStoryRepository.clearAllNativeStories()
        prefsRepository.saveBoolean(IS_READY, false)
        dataStoreRepository.saveLong(LAST_SYNC_TIME, 0)
        dataStoreRepository.saveBoolean(IS_SYNC_COMPLETED, false)
        dataStoreRepository.saveDouble("${WORDS_VERSION}${targetLang}_beginner", 0.0)
        dataStoreRepository.saveDouble("${WORDS_VERSION}${targetLang}_essential", 0.0)
        dataStoreRepository.saveDouble("${STORY_VERSION}${targetLang}_beginner", 0.0)
        dataStoreRepository.saveDouble("${STORY_VERSION}${targetLang}_essential", 0.0)
        dataStoreRepository.saveDouble("${NATIVE_WORD_VERSION}${targetLang}_${nativeLang}_beginner", 0.0)
        dataStoreRepository.saveDouble("${NATIVE_WORD_VERSION}${targetLang}_${nativeLang}_essential", 0.0)
        dataStoreRepository.saveDouble("${NATIVE_STORY_VERSION}${targetLang}_beginner_${nativeLang}", 0.0)
        dataStoreRepository.saveDouble("${NATIVE_STORY_VERSION}${targetLang}_essential_${nativeLang}", 0.0)
    }

    private suspend fun wordSync(collection: String, estimatedSize: Int): Boolean {

        val localVersion = dataStoreRepository.getDouble("${WORDS_VERSION}${targetLang}_$collection").firstOrNull() ?: 0.0

        val version = remoteWordRepository.getWordVersion(targetLang, collection)

        if (localVersion < version) {

            val remoteWords = remoteWordRepository.fetchWord(targetLang, collection)

            val remoteCount = remoteWords.values.sumOf { unitMap ->
                unitMap.values.sumOf { it.size }
            }

            if (remoteCount != estimatedSize) {
                Logger.d(SyncDataUseCase::class.java.simpleName, "Failed: Remote count: $remoteCount, Estimated size: $estimatedSize")
                return false
            }

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
            val insertedWords = wordRepository.getWordsByCollectionId(collectionId)
            if (insertedWords.size != estimatedSize) {
                Logger.d(SyncDataUseCase::class.java.simpleName, "Failed: Inserted ${insertedWords.size} expected: $estimatedSize")
                return false
            }
            dataStoreRepository.saveDouble("${WORDS_VERSION}${targetLang}_$collection", version)

            Logger.d(SyncDataUseCase::class.java.simpleName, "Inserted ${wordEntities.size} words for $collection")
            return true

        } else {
            Logger.d(SyncDataUseCase::class.java.simpleName, "No update needed for $collection")
            return true
        }
    }

    private suspend fun storySync(collection: String, estimatedSize: Int): Boolean {

        val localVersion = dataStoreRepository.getDouble("${STORY_VERSION}${targetLang}_$collection").firstOrNull() ?: 0.0

        val version = remoteStoryRepository.getStoryVersion(targetLang, collection)

        if (localVersion < version) {

            val remoteStories = remoteStoryRepository.fetchStory(targetLang, collection)

            val remoteCount = remoteStories.values.sumOf { unitMap ->
                unitMap.values.sumOf { it.size }
            }

            if (remoteCount != estimatedSize) {
                Logger.d(SyncDataUseCase::class.java.simpleName, "Failed: Remote count: $remoteCount, Estimated size: $estimatedSize")
                return false
            }

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
            val insertedStories = storyRepository.getStoriesByCollectionId(collectionId)
            if (insertedStories.size != estimatedSize) {
                Logger.d(SyncDataUseCase::class.java.simpleName, "Failed: Inserted ${insertedStories.size} expected: $estimatedSize")
                return false
            }
            dataStoreRepository.saveDouble("${WORDS_VERSION}${targetLang}_$collection", version)

            Logger.d(SyncDataUseCase::class.java.simpleName, "Inserted ${storyEntities.size} stories for $collection")
            return true

        } else {
            Logger.d(SyncDataUseCase::class.java.simpleName, "No update needed for $collection")
            return true
        }
    }

    private suspend fun nativeWordSync(collection: String, estimatedSize: Int): Boolean {

        val localVersion = dataStoreRepository.getDouble("${NATIVE_WORD_VERSION}${targetLang}_${nativeLang}_$collection").firstOrNull() ?: 0.0

        val version = remoteNativeWordRepository.getNativeWordVersion(targetLang, collection, nativeLang)

        if (localVersion < version) {

            val remoteNativeWords = remoteNativeWordRepository.fetchNativeWord(targetLang, collection, nativeLang)

            val remoteCount = remoteNativeWords.values.sumOf { unitMap ->
                unitMap.values.sumOf { it.size }
            }

            if (remoteCount != estimatedSize) {
                Logger.d(SyncDataUseCase::class.java.simpleName, "Failed: Remote count: $remoteCount, Estimated size: $estimatedSize")
                return false
            }

            val nativeWordEntities = mutableListOf<NativeWord>()

            val collectionId = WordCollection.Companion.fromKey(collection)?.id
                ?: throw IllegalArgumentException("Unknown collection: $collection")

            for ((partId, unitMap) in remoteNativeWords) {
                for ((unitId, wordList) in unitMap) {
                    wordList.forEach { nativeWord ->
                        nativeWordId++
                        nativeWordEntities.add(
                            // ayni damda u single native languageni qo'llab quvvatlagani uchun
                            // bevosita wordId bilan nativeWordId o'zaro teng bo'lgani
                            // uchun shunday kiritib ketilgan
                            nativeWord.copy(id = nativeWordId, collectionId = collectionId, partId = partId, unitId = unitId, nativeLanguage = nativeLang)
                        )
                    }
                }
            }

            nativeWordRepository.insertNativeWords(nativeWordEntities)
            val insertedNativeWords = nativeWordRepository.getNativeWordsByCollectionId(collectionId)
            if (insertedNativeWords.size != estimatedSize) {
                Logger.d(SyncDataUseCase::class.java.simpleName, "Failed: Inserted ${insertedNativeWords.size} expected: $estimatedSize")
                return false
            }
            dataStoreRepository.saveDouble("${NATIVE_WORD_VERSION}${targetLang}_${nativeLang}_$collection", version)

            Logger.d(SyncDataUseCase::class.java.simpleName, "Inserted ${nativeWordEntities.size} native words for $collection")
            return true

        } else {
            Logger.d(SyncDataUseCase::class.java.simpleName, "No update needed for $collection")
            return true
        }
    }

    private suspend fun nativeStorySync(collection: String, estimatedSize: Int): Boolean {

        val localVersion = dataStoreRepository
            .getDouble("$NATIVE_STORY_VERSION${targetLang}_${collection}_$nativeLang")
            .firstOrNull() ?: 0.0


        val version = remoteNativeStoryRepository.getNativeStoryVersion(targetLang, collection, nativeLang)

        if (localVersion < version) {

            val remoteNativeStories = remoteNativeStoryRepository.fetchNativeStory(targetLang, collection, nativeLang)

            val remoteCount = remoteNativeStories.values.sumOf { unitMap ->
                unitMap.values.sumOf { it.size }
            }

            if (remoteCount != estimatedSize) {
                Logger.d(SyncDataUseCase::class.java.simpleName, "Failed: Remote count: $remoteCount, Estimated size: $estimatedSize")
                return false
            }

            val nativeStoryEntities = mutableListOf<NativeStory>()

            val collectionId = WordCollection.Companion.fromKey(collection)?.id
                ?: throw IllegalArgumentException("Unknown collection: $collection")

            for ((partId, unitMap) in remoteNativeStories) {
                for ((unitId, storyList) in unitMap) {
                    for ((_, nativeStory) in storyList) {
                        nativeStoryId++
                        nativeStoryEntities.add(
                            // ayni damda u single native languageni qo'llab quvvatlagani uchun
                            // bevosita wordId bilan nativeWordId o'zaro teng bo'lgani
                            // uchun shunday kiritib ketilgan
                            nativeStory.copy(id = nativeStoryId, collectionId = collectionId, partId = partId, unitId = unitId, nativeLanguage = nativeLang)
                        )
                    }
                }
            }

            nativeStoryRepository.insertNativeStories(nativeStoryEntities)
            val insertedNativeStories = nativeStoryRepository.getNativeStoriesByCollectionId(collectionId)
            if (insertedNativeStories.size != estimatedSize) {
                Logger.d(SyncDataUseCase::class.java.simpleName, "Failed: Inserted ${insertedNativeStories.size} expected: $estimatedSize")
                return false
            }
            dataStoreRepository.saveDouble("${NATIVE_STORY_VERSION}${targetLang}_${collection}_${nativeLang}", version)

            Logger.d(SyncDataUseCase::class.java.simpleName, "Inserted ${nativeStoryEntities.size} native stories for $collection")
            return true

        } else {
            Logger.d(SyncDataUseCase::class.java.simpleName, "No update needed for $collection")
            return true
        }
    }

    private suspend fun setupScore(estimatedSize: Int): Boolean {

        val nativeWords = nativeWordRepository.getAllNativeWords()
        if (nativeWords.size != estimatedSize) {
            Logger.d(SyncDataUseCase::class.java.simpleName, "Native words size mismatch! nativeWords size: ${nativeWords.size} expected: $estimatedSize")
            return false
        }

        val scores = scoreRepository.getAllScores()
        if (scores.size == estimatedSize) {
            Logger.d(SyncDataUseCase::class.java.simpleName, "Scores already setup!")
            return true
        }

        if (nativeWords.isNotEmpty()) {

            val scores = mutableListOf<Score>()

            for (word in nativeWords) {
                // bu yerda Score NativeWord id ga asoslanadiva ularni o'zaro id si bir xil
                scores.add(Score(id = word.id!!, collectionId = word.collectionId, partId = word.partId, unitId = word.unitId, nativeWordId = word.id))
            }

            scoreRepository.insertScores(scores)

            val insertedScores = scoreRepository.getAllScores()
            if (insertedScores.size != estimatedSize) {
                Logger.d(SyncDataUseCase::class.java.simpleName, "Failed: Inserted ${insertedScores.size} expected: $estimatedSize")
                return false
            }

            Logger.d(SyncDataUseCase::class.java.simpleName, "Inserted ${scores.size} scores")
            return true
        } else {
            Logger.d(SyncDataUseCase::class.java.simpleName, "No native words found!")
            return false
        }
    }

    private suspend fun migrate(estimatedSize: Int): Boolean {

        val isMigrated = dataStoreRepository.getBoolean(IS_LEGACY_DB_MIGRATED).firstOrNull() ?: false

        if (isMigrated) {
            Logger.d(SyncDataUseCase::class.java.simpleName, "Legacy Database already migrated!")
            return true
        }

        val legacyDatabase = LegacyDatabase.Companion.getInstance(context)
        val legacyDao = legacyDatabase.wordDao()
        val legacyWords = legacyDao.getWords()

        if (legacyWords.isEmpty()) {
            Logger.d(SyncDataUseCase::class.java.simpleName, "No legacy data found!")
            return true
        }

        if (legacyWords.size != estimatedSize) {
            Logger.d(SyncDataUseCase::class.java.simpleName, "Legacy data size mismatch!")
            return false
        }

        legacyWords.forEach { word ->

            val targetId = word.id + 1601
            val score = scoreRepository.getScoreById(targetId) ?: return@forEach

            when {
                word.level > 0 -> {
                    scoreRepository.updateScore(score.copy(correctCount = word.level))
                }
                word.level < 0 -> {
                    scoreRepository.updateScore(score.copy(incorrectCount = -word.level))
                }
            }
        }

        dataStoreRepository.saveBoolean(IS_LEGACY_DB_MIGRATED, true)

        legacyDao.clear()

        LegacyDatabase.Companion.destroyInstance()

        Logger.d(SyncDataUseCase::class.java.simpleName, "Legacy Database migrated!")
        return true
    }
}