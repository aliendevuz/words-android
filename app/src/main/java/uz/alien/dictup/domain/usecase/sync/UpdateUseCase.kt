package uz.alien.dictup.domain.usecase.sync

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import okio.IOException
import uz.alien.dictup.data.local.legacy.LegacyDatabase
import uz.alien.dictup.data.mapper.toNativeStory
import uz.alien.dictup.data.mapper.toNativeWord
import uz.alien.dictup.data.mapper.toStory
import uz.alien.dictup.data.mapper.toWord
import uz.alien.dictup.data.remote.retrofit.dto.StoryDto
import uz.alien.dictup.data.remote.retrofit.dto.WordDto
import uz.alien.dictup.domain.model.NativeStory
import uz.alien.dictup.domain.model.NativeWord
import uz.alien.dictup.domain.model.Score
import uz.alien.dictup.domain.model.Story
import uz.alien.dictup.domain.model.Word
import uz.alien.dictup.domain.repository.AssetsManagerRepository
import uz.alien.dictup.domain.repository.CacheManagerRepository
import uz.alien.dictup.domain.repository.DataStoreRepository
import uz.alien.dictup.domain.repository.room.NativeStoryRepository
import uz.alien.dictup.domain.repository.room.NativeWordRepository
import uz.alien.dictup.domain.repository.room.ScoreRepository
import uz.alien.dictup.domain.repository.room.StoryRepository
import uz.alien.dictup.domain.repository.room.WordRepository
import uz.alien.dictup.utils.Logger
import uz.alien.dictup.value.strings.DataStore.IS_LEGACY_DB_MIGRATED
import uz.alien.dictup.value.strings.DataStore.LOCAL_NATIVE_STORY_VERSION
import uz.alien.dictup.value.strings.DataStore.LOCAL_NATIVE_WORD_VERSION
import uz.alien.dictup.value.strings.DataStore.LOCAL_STORY_VERSION
import uz.alien.dictup.value.strings.DataStore.LOCAL_WORDS_VERSION
import uz.alien.dictup.value.strings.DataStore.NATIVE_STORY_VERSION
import uz.alien.dictup.value.strings.DataStore.NATIVE_WORD_VERSION
import uz.alien.dictup.value.strings.DataStore.STORY_VERSION
import uz.alien.dictup.value.strings.DataStore.WORD_VERSION

class UpdateUseCase(
    private val context: Context,
    private val wordRepository: WordRepository,
    private val nativeWordRepository: NativeWordRepository,
    private val storyRepository: StoryRepository,
    private val nativeStoryRepository: NativeStoryRepository,
    private val scoreRepository: ScoreRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val cacheManagerRepository: CacheManagerRepository,
    private val assetsManagerRepository: AssetsManagerRepository
) {

    private val ASSETS_VERSION = 1.0

    suspend operator fun invoke() {

        syncWords(
            versionKey = "${WORD_VERSION}en_beginner",
            localVersionKey = "${LOCAL_WORDS_VERSION}en_beginner",
            loadCache = { cacheManagerRepository.loadBeginnerWords() },
            clearCache = { cacheManagerRepository.clearBeginnerWords() },
            loadAssets = { assetsManagerRepository.loadBeginnerWords() },
            collectionId = 0,
            startId = 0,
            isNative = false,
            nativeLang = null
        )

        syncWords(
            versionKey = "${NATIVE_WORD_VERSION}en_beginner",
            localVersionKey = "${LOCAL_NATIVE_WORD_VERSION}en_beginner",
            loadCache = { cacheManagerRepository.loadBeginnerUzWords() },
            clearCache = { cacheManagerRepository.clearBeginnerUzWords() },
            loadAssets = { assetsManagerRepository.loadBeginnerUzWords() },
            collectionId = 0,
            startId = 0,
            isNative = true,
            nativeLang = "uz"
        )

        syncWords(
            versionKey = "${WORD_VERSION}en_essential",
            localVersionKey = "${LOCAL_WORDS_VERSION}en_essential",
            loadCache = { cacheManagerRepository.loadEssentialWords() },
            clearCache = { cacheManagerRepository.clearEssentialWords() },
            loadAssets = { assetsManagerRepository.loadEssentialWords() },
            collectionId = 1,
            startId = 1600,
            isNative = false,
            nativeLang = null
        )

        syncWords(
            versionKey = "${NATIVE_WORD_VERSION}en_essential",
            localVersionKey = "${LOCAL_NATIVE_WORD_VERSION}en_essential",
            loadCache = { cacheManagerRepository.loadEssentialUzWords() },
            clearCache = { cacheManagerRepository.clearEssentialUzWords() },
            loadAssets = { assetsManagerRepository.loadEssentialUzWords() },
            collectionId = 1,
            startId = 1600,
            isNative = true,
            nativeLang = "uz"
        )

        syncStories(
            versionKey = "${STORY_VERSION}en_beginner",
            localVersionKey = "${LOCAL_STORY_VERSION}en_beginner",
            loadCache = { cacheManagerRepository.loadBeginnerStories() },
            clearCache = { cacheManagerRepository.clearBeginnerStories() },
            loadAssets = { assetsManagerRepository.loadBeginnerStories() },
            collectionId = 0,
            startId = 0,
            isNative = false,
            nativeLang = null
        )

        syncStories(
            versionKey = "${NATIVE_STORY_VERSION}en_beginner",
            localVersionKey = "${LOCAL_NATIVE_STORY_VERSION}en_beginner",
            loadCache = { cacheManagerRepository.loadBeginnerUzStories() },
            clearCache = { cacheManagerRepository.clearBeginnerUzStories() },
            loadAssets = { assetsManagerRepository.loadBeginnerUzStories() },
            collectionId = 0,
            startId = 0,
            isNative = true,
            nativeLang = "uz"
        )

        syncStories(
            versionKey = "${STORY_VERSION}en_essential",
            localVersionKey = "${LOCAL_STORY_VERSION}en_essential",
            loadCache = { cacheManagerRepository.loadEssentialStories() },
            clearCache = { cacheManagerRepository.clearEssentialStories() },
            loadAssets = { assetsManagerRepository.loadEssentialStories() },
            collectionId = 1,
            startId = 240,
            isNative = false,
            nativeLang = null
        )

        syncStories(
            versionKey = "${NATIVE_STORY_VERSION}en_essential",
            localVersionKey = "${LOCAL_NATIVE_STORY_VERSION}en_essential",
            loadCache = { cacheManagerRepository.loadEssentialUzStories() },
            clearCache = { cacheManagerRepository.clearEssentialUzStories() },
            loadAssets = { assetsManagerRepository.loadEssentialUzStories() },
            collectionId = 1,
            startId = 240,
            isNative = true,
            nativeLang = "uz"
        )

        setupScore()
        migrate()
    }

    private suspend fun syncWords(
        versionKey: String,
        localVersionKey: String,
        loadCache: suspend () -> String,
        clearCache: suspend () -> Unit,
        loadAssets: suspend () -> String,
        collectionId: Int,
        startId: Int,
        isNative: Boolean,
        nativeLang: String?
    ) {
        val version = dataStoreRepository.getDouble(versionKey).firstOrNull() ?: 0.0
        val localVersion = dataStoreRepository.getDouble(localVersionKey).firstOrNull() ?: 0.0
        var id = startId

        if (localVersion >= maxOf(version, ASSETS_VERSION)) {
            Logger.d("SyncSkip", "Already up to date for $versionKey (local: $localVersion, remote: $version)")
            return
        }

        if (version > localVersion) {
            try {
                val content = loadCache()
                val words = parseWords(content)
                val entities = mutableListOf<Any>()

                for ((partId, unitMap) in words) {
                    for ((unitId, wordList) in unitMap) {
                        wordList.forEach { wordDto ->
                            val entity = if (isNative) {
                                wordDto.toNativeWord(nativeLang!!).copy(id = ++id, collectionId = collectionId, partId = partId, unitId = unitId)
                            } else {
                                wordDto.toWord().copy(id = ++id, collectionId = collectionId, partId = partId, unitId = unitId)
                            }
                            entities.add(entity)
                        }
                    }
                }

                if (isNative) {
                    nativeWordRepository.insertNativeWords(entities as List<NativeWord>)
                } else {
                    wordRepository.insertWords(entities as List<Word>)
                }

                dataStoreRepository.saveDouble(localVersionKey, version)
                clearCache()
                Logger.d("SyncSuccess", "Words synced from cache for $versionKey")
            } catch (e: Exception) {
                Logger.w("cache", "Error syncing words from cache for $versionKey: ${e.stackTraceToString()}")
            }
        } else {
            try {
                val content = loadAssets()
                val words = parseWords(content)
                val entities = mutableListOf<Any>()

                for ((partId, unitMap) in words) {
                    for ((unitId, wordList) in unitMap) {
                        wordList.forEach { wordDto ->
                            val entity = if (isNative) {
                                wordDto.toNativeWord(nativeLang!!).copy(id = ++id, collectionId = collectionId, partId = partId, unitId = unitId)
                            } else {
                                wordDto.toWord().copy(id = ++id, collectionId = collectionId, partId = partId, unitId = unitId)
                            }
                            entities.add(entity)
                        }
                    }
                }

                if (isNative) {
                    nativeWordRepository.insertNativeWords(entities as List<NativeWord>)
                } else {
                    wordRepository.insertWords(entities as List<Word>)
                }

                dataStoreRepository.saveDouble(localVersionKey, ASSETS_VERSION)
                Logger.d("SyncSuccess", "Words loaded from assets for $versionKey")
            } catch (e: Exception) {
                Logger.w("assets", "Error loading words from assets for $versionKey: ${e.stackTraceToString()}")
            }
        }

//        val logId = startId + 1
//        val logEntity = if (isNative) {
//            nativeWordRepository.getNativeWordById(logId)
//        } else {
//            wordRepository.getWordById(logId)
//        }
//        Logger.d("SyncSuccess", "Word ID $logId: $logEntity")
        if (isNative) {
            nativeWordRepository.getAllNativeWords().forEach {
                Logger.d("words", it.toString())
            }
        } else {
            wordRepository.getAllWords().forEach {
                Logger.d("words", it.toString())
            }
        }
    }

    private suspend fun syncStories(
        versionKey: String,
        localVersionKey: String,
        loadCache: suspend () -> String,
        clearCache: suspend () -> Unit,
        loadAssets: suspend () -> String,
        collectionId: Int,
        startId: Int,
        isNative: Boolean,
        nativeLang: String?
    ) {
        val version = dataStoreRepository.getDouble(versionKey).firstOrNull() ?: 0.0
        val localVersion = dataStoreRepository.getDouble(localVersionKey).firstOrNull() ?: 0.0
        var id = startId

        if (localVersion >= maxOf(version, ASSETS_VERSION)) {
            Logger.d("SyncSkip", "Already up to date for $versionKey (local: $localVersion, remote: $version)")
            return
        }

        if (version > localVersion) {
            try {
                val content = loadCache()
                val stories = parseStories(content)
                val entities = mutableListOf<Any>()

                for ((partId, unitMap) in stories) {
                    for ((unitId, storyMap) in unitMap) {
                        for ((_, storyDto) in storyMap) {
                            val entity = if (isNative) {
                                storyDto.toNativeStory(nativeLang!!).copy(id = ++id, collectionId = collectionId, partId = partId, unitId = unitId)
                            } else {
                                storyDto.toStory().copy(id = ++id, collectionId = collectionId, partId = partId, unitId = unitId)
                            }
                            entities.add(entity)
                        }
                    }
                }

                if (isNative) {
                    nativeStoryRepository.insertNativeStories(entities as List<NativeStory>)
                } else {
                    storyRepository.insertStories(entities as List<Story>)
                }

                dataStoreRepository.saveDouble(localVersionKey, version)
                clearCache()
                Logger.d("SyncSuccess", "Stories synced from cache for $versionKey")
            } catch (e: Exception) {
                Logger.w("cache", "Error syncing stories from cache for $versionKey: ${e.stackTraceToString()}")
            }
        } else {
            try {
                val content = loadAssets()
                val stories = parseStories(content)
                val entities = mutableListOf<Any>()

                for ((partId, unitMap) in stories) {
                    for ((unitId, storyMap) in unitMap) {
                        for ((_, storyDto) in storyMap) {
                            val entity = if (isNative) {
                                storyDto.toNativeStory(nativeLang!!).copy(id = ++id, collectionId = collectionId, partId = partId, unitId = unitId)
                            } else {
                                storyDto.toStory().copy(id = ++id, collectionId = collectionId, partId = partId, unitId = unitId)
                            }
                            entities.add(entity)
                        }
                    }
                }

                if (isNative) {
                    nativeStoryRepository.insertNativeStories(entities as List<NativeStory>)
                } else {
                    storyRepository.insertStories(entities as List<Story>)
                }

                dataStoreRepository.saveDouble(localVersionKey, ASSETS_VERSION)
                Logger.d("SyncSuccess", "Stories loaded from assets for $versionKey")
            } catch (e: Exception) {
                Logger.w("assets", "Error loading stories from assets for $versionKey: ${e.stackTraceToString()}")
            }
        }

        val logId = startId + 1
        val logEntity = if (isNative) {
            nativeStoryRepository.getNativeStoryById(logId)
        } else {
            storyRepository.getStoryById(logId)
        }
        Logger.d("SyncSuccess", "Story ID $logId: $logEntity")
    }

    private suspend fun setupScore() {

        if (scoreRepository.getAllScores().size == 5200) return

        val nativeWords = nativeWordRepository.getAllNativeWords()
        val scores = mutableListOf<Score>()

        nativeWords.forEach {
            scores.add(Score(id = it.id!! , collectionId = it.collectionId!!, partId = it.partId!!, unitId = it.unitId!!, nativeWordId = it.id))
        }

        scoreRepository.insertScores(scores)
    }

    private suspend fun migrate() {

        val isMigrated = dataStoreRepository.getBoolean(IS_LEGACY_DB_MIGRATED).firstOrNull() ?: false

        if (isMigrated) return

        val legacyDatabase = LegacyDatabase.Companion.getInstance(context)
        val legacyDao = legacyDatabase.wordDao()
        val legacyWords = legacyDao.getWords()

        legacyWords.forEach {

            Logger.d("Score: ${it.level}")

            val score = scoreRepository.getScoreById(it.id + 1601) ?: return@forEach

            when {
                it.level > 0 -> {
                    scoreRepository.updateScore(score.copy(correctCount = it.level))
                }
                it.level < 0 -> {
                    scoreRepository.updateScore(score.copy(incorrectCount = -it.level))
                }
            }

            dataStoreRepository.saveBoolean(IS_LEGACY_DB_MIGRATED, true)

            legacyDao.clear()

            LegacyDatabase.destroyInstance()
        }
    }

    fun parseWords(content: String): Map<Int, Map<Int, List<WordDto>>> {
        val gson = Gson()
        val type = object : TypeToken<Map<Int, Map<Int, List<WordDto>>>>() {}.type
        return gson.fromJson(content, type)
    }

    fun parseStories(content: String): Map<Int, Map<Int, Map<Int, StoryDto>>> {
        val gson = Gson()
        val type = object : TypeToken<Map<Int, Map<Int, Map<Int, StoryDto>>>>() {}.type
        return gson.fromJson(content, type)
    }
}