package uz.alien.dictup.domain.repository.retrofit

import uz.alien.dictup.domain.model.NativeStory

interface RemoteNativeStoryRepository {

    suspend fun fetchNativeStory(tagetLang: String, collection: String, nativeLang: String): Map<Int, Map<Int, Map<Int, NativeStory>>>

    suspend fun getNativeStoryVersion(tagetLang: String, collection: String, nativeLang: String): Double
}