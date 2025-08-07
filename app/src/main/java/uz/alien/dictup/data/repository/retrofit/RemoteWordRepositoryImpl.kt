package uz.alien.dictup.data.repository.retrofit

import uz.alien.dictup.data.mapper.toWord
import uz.alien.dictup.data.remote.retrofit.api.WordApi
import uz.alien.dictup.domain.model.Word
import uz.alien.dictup.domain.repository.retrofit.RemoteWordRepository

class RemoteWordRepositoryImpl(
    private val api: WordApi
): RemoteWordRepository {

    override suspend fun fetchWord(targetLang: String, collection: String): Map<Int, Map<Int, List<Word>>> {
        val dto = api.getWords(targetLang, collection)
        return dto.mapValues { (_, inner) ->
            inner.mapValues { (_, list) ->
                list.map { wordDto ->
                    wordDto.toWord()
                }
            }
        }
    }

    override suspend fun getWordVersion(targetLang: String, collection: String): Double {
        return api.getWordsVersion(targetLang, collection)
    }
}