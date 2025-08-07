package uz.alien.dictup.domain.usecase.on_internet_connected

import uz.alien.dictup.core.utils.Logger
import uz.alien.dictup.data.mapper.toWordEntity
import uz.alien.dictup.domain.model.Word
import uz.alien.dictup.domain.repository.DataStoreRepository
import uz.alien.dictup.domain.repository.retrofit.RemoteNativeStoryRepository
import uz.alien.dictup.domain.repository.retrofit.RemoteNativeWordRepository
import uz.alien.dictup.domain.repository.retrofit.RemoteStoryRepository
import uz.alien.dictup.domain.repository.retrofit.RemoteWordRepository
import uz.alien.dictup.domain.repository.room.WordRepository
import uz.alien.dictup.shared.WordCollection

class SyncDataUseCase(
    private val remoteWordRepository: RemoteWordRepository,
    private val remoteStoryRepository: RemoteStoryRepository,
    private val remoteNativeWordRepository: RemoteNativeWordRepository,
    private val remoteNativeStoryRepository: RemoteNativeStoryRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val wordRepository: WordRepository,
) {

    private val targetLang = "en"
    private val collections = listOf(
        "beginner",
        "essential"
    )
    private val nativeLang = "uz"

    suspend operator fun invoke() {
        try {
            for (collection in collections) {

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
                                wordEntities.add(
                                    word.copy(collectionId = collectionId, partId = partId, unitId = unitId)
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
        } catch (e: retrofit2.HttpException) {
            Logger.d("HTTP Exception: ${e.response().toString()}")
        } catch (e: Exception) {
            Logger.d("Unexpected error: ${e.localizedMessage}")
        }
    }
}