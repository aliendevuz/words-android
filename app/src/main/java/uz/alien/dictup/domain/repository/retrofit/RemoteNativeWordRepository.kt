package uz.alien.dictup.domain.repository.retrofit

import uz.alien.dictup.domain.model.NativeWord

interface RemoteNativeWordRepository {

    suspend fun fetchNativeWord(tagetLang: String, collection: String, nativeLang: String): Map<Int, Map<Int, List<NativeWord>>>

    suspend fun getNativeWordVersion(tagetLang: String, collection: String, nativeLang: String): Double
}