package uz.alien.dictup.domain.repository.retrofit

import uz.alien.dictup.domain.model.Word

interface RemoteWordRepository {

    suspend fun fetchWord(targetLang: String, collection: String): Map<Int, Map<Int, List<Word>>>

    suspend fun getWordVersion(targetLang: String, collection: String): Double
}