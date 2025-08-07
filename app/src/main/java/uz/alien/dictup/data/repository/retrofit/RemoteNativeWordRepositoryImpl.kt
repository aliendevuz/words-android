package uz.alien.dictup.data.repository.retrofit

import uz.alien.dictup.data.mapper.toNativeWord
import uz.alien.dictup.data.remote.retrofit.api.NativeWordApi
import uz.alien.dictup.domain.model.NativeWord
import uz.alien.dictup.domain.repository.retrofit.RemoteNativeWordRepository

class RemoteNativeWordRepositoryImpl(
    private val api: NativeWordApi
) : RemoteNativeWordRepository {

    override suspend fun fetchNativeWord(tagetLang: String, collection: String, nativeLang: String): Map<Int, Map<Int, List<NativeWord>>> {
        val dto = api.getNativeWords(tagetLang, collection, nativeLang)
        return dto.mapValues { (_, inner) ->
            inner.mapValues { (_, list) ->
                list.map { wordDto ->
                    wordDto.toNativeWord(nativeLang)
                }
            }
        }
    }

    override suspend fun getNativeWordVersion(tagetLang: String, collection: String, nativeLang: String): Double {
        return api.getNativeWordsVersion(tagetLang, collection, nativeLang)
    }
}