package uz.alien.dictup.domain.usecase.sync

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import uz.alien.dictup.BuildConfig
import uz.alien.dictup.domain.repository.CacheManagerRepository
import uz.alien.dictup.domain.repository.DataStoreRepository
import uz.alien.dictup.domain.repository.HttpManager
import uz.alien.dictup.utils.Logger
import uz.alien.dictup.value.strings.DataStore
import uz.alien.dictup.value.strings.DataStore.NATIVE_STORY_VERSION
import uz.alien.dictup.value.strings.DataStore.NATIVE_WORD_VERSION
import uz.alien.dictup.value.strings.DataStore.STORY_VERSION
import uz.alien.dictup.value.strings.DataStore.WORD_VERSION
import java.security.MessageDigest

class SyncDataUseCase(
    private val httpManager: HttpManager,
    private val dataStoreRepository: DataStoreRepository,
    private val cacheManagerRepository: CacheManagerRepository
) {

    suspend operator fun invoke() {

        val lastSyncTime = dataStoreRepository.getLong(DataStore.LAST_SYNC_TIME).firstOrNull()
        if (lastSyncTime == null) {

            if (startSync()) dataStoreRepository.saveLong(DataStore.LAST_SYNC_TIME, System.currentTimeMillis())

        } else {

            val currentTime = System.currentTimeMillis()
            val hour = 60 // 1000 * 60 * 60 = for hour

            if (lastSyncTime + hour < currentTime) {

                if (startSync()) dataStoreRepository.saveLong(DataStore.LAST_SYNC_TIME, System.currentTimeMillis())

            } else {

                Logger.d(SyncDataUseCase::class.java.simpleName, "No update needed $lastSyncTime")
            }
        }
    }

    private suspend fun startSync(): Boolean {
        return listOf(
            syncData(
                versionUrl = "${BuildConfig.BASE_URL}assets/.v/en/beginner/words.json",
                hashUrl = "${BuildConfig.BASE_URL}assets/.hash/en/beginner/words.json.sha256",
                fileUrl = "${BuildConfig.BASE_URL}assets/en/beginner/words.json",
                versionKey = "${WORD_VERSION}en_beginner",
                saveData = { cacheManagerRepository.saveBeginnerWords(it) }
            ),
            syncData(
                versionUrl = "${BuildConfig.BASE_URL}assets/.v/en/beginner/uz/words.json",
                hashUrl = "${BuildConfig.BASE_URL}assets/.hash/en/beginner/uz/words.json.sha256",
                fileUrl = "${BuildConfig.BASE_URL}assets/en/beginner/uz/words.json",
                versionKey = "${NATIVE_WORD_VERSION}en_beginner",
                saveData = { cacheManagerRepository.saveBeginnerUzWords(it) }
            ),
            syncData(
                versionUrl = "${BuildConfig.BASE_URL}assets/.v/en/essential/words.json",
                hashUrl = "${BuildConfig.BASE_URL}assets/.hash/en/essential/words.json.sha256",
                fileUrl = "${BuildConfig.BASE_URL}assets/en/essential/words.json",
                versionKey = "${WORD_VERSION}en_essential",
                saveData = { cacheManagerRepository.saveEssentialWords(it) }
            ),
            syncData(
                versionUrl = "${BuildConfig.BASE_URL}assets/.v/en/essential/uz/words.json",
                hashUrl = "${BuildConfig.BASE_URL}assets/.hash/en/essential/uz/words.json.sha256",
                fileUrl = "${BuildConfig.BASE_URL}assets/en/essential/uz/words.json",
                versionKey = "${NATIVE_WORD_VERSION}en_essential",
                saveData = { cacheManagerRepository.saveEssentialUzWords(it) }
            ),
            syncData(
                versionUrl = "${BuildConfig.BASE_URL}assets/.v/en/beginner/stories.json",
                hashUrl = "${BuildConfig.BASE_URL}assets/.hash/en/beginner/stories.json.sha256",
                fileUrl = "${BuildConfig.BASE_URL}assets/en/beginner/stories.json",
                versionKey = "${STORY_VERSION}en_beginner",
                saveData = { cacheManagerRepository.saveBeginnerStories(it) }
            ),
            syncData(
                versionUrl = "${BuildConfig.BASE_URL}assets/.v/en/beginner/uz/stories.json",
                hashUrl = "${BuildConfig.BASE_URL}assets/.hash/en/beginner/uz/stories.json.sha256",
                fileUrl = "${BuildConfig.BASE_URL}assets/en/beginner/uz/stories.json",
                versionKey = "${NATIVE_STORY_VERSION}en_beginner",
                saveData = { cacheManagerRepository.saveBeginnerUzStories(it) }
            ),
            syncData(
                versionUrl = "${BuildConfig.BASE_URL}assets/.v/en/essential/stories.json",
                hashUrl = "${BuildConfig.BASE_URL}assets/.hash/en/essential/stories.json.sha256",
                fileUrl = "${BuildConfig.BASE_URL}assets/en/essential/stories.json",
                versionKey = "${STORY_VERSION}en_essential",
                saveData = { cacheManagerRepository.saveEssentialStories(it) }
            ),
            syncData(
                versionUrl = "${BuildConfig.BASE_URL}assets/.v/en/essential/uz/stories.json",
                hashUrl = "${BuildConfig.BASE_URL}assets/.hash/en/essential/uz/stories.json.sha256",
                fileUrl = "${BuildConfig.BASE_URL}assets/en/essential/uz/stories.json",
                versionKey = "${NATIVE_STORY_VERSION}en_essential",
                saveData = { cacheManagerRepository.saveEssentialUzStories(it) }
            )
        ).all { it }
    }

    private suspend fun syncData(
        versionUrl: String,
        hashUrl: String,
        fileUrl: String,
        versionKey: String,
        saveData: (String) -> Unit
    ): Boolean {
        // Local versiyani tekshirish
        val localVersion = dataStoreRepository.getDouble(versionKey).firstOrNull() ?: 0.0

        // Remote versiyani olish
        var remoteVersion = 0.0
        retryRequest { httpManager.get(versionUrl) }.fold(
            onSuccess = { remoteVersion = it.trim().toDouble() },
            onFailure = {
                Logger.w("SyncData", "Version fetch error for $versionKey: ${it.message}")
                return false
            }
        )

        // Agar remoteVersion <= localVersion, skip qilamiz
        if (remoteVersion <= localVersion) {
            Logger.d("SyncData", "No update needed for $versionKey (local: $localVersion, remote: $remoteVersion)")
            return true
        }

        // Hash olish
        var expectedHash = ""
        retryRequest { httpManager.get(hashUrl) }.fold(
            onSuccess = { expectedHash = it.trim() },
            onFailure = {
                Logger.w("SyncData", "Hash fetch error for $versionKey: ${it.message}")
                return false
            }
        )

        // Faylni yuklash
        retryRequest { httpManager.getBytes(fileUrl) }.fold(
            onSuccess = { bytes ->
                val success = bytes.calculateHash(expectedHash)
                if (success) {
                    saveData(bytes.toString(Charsets.UTF_8))
                    dataStoreRepository.saveDouble(versionKey, remoteVersion)
                    Logger.d("SyncData", "File synced successfully for $versionKey")
                    return true
                } else {
                    Logger.w("SyncData", "File is damaged for $versionKey")
                    return false
                }
            },
            onFailure = {
                Logger.w("SyncData", "File fetch error for $versionKey: ${it.message}")
                return false
            }
        )
    }

    suspend fun <T> retryRequest(
        times: Int = 3,
        initialDelay: Long = 1000L,
        maxDelay: Long = 8000L,
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
        return block()
    }

    fun ByteArray.calculateHash(expectedHash: String): Boolean {
        val digest = MessageDigest.getInstance("SHA-256")
        val actualHash = digest.digest(this).joinToString("") { "%02x".format(it) }
        return actualHash.equals(expectedHash.trim(), ignoreCase = true)
    }
}