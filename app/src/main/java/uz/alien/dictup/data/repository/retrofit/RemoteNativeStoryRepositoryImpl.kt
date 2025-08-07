package uz.alien.dictup.data.repository.retrofit

import uz.alien.dictup.data.mapper.toNativeStory
import uz.alien.dictup.data.remote.retrofit.api.NativeStoryApi
import uz.alien.dictup.domain.model.NativeStory
import uz.alien.dictup.domain.repository.retrofit.RemoteNativeStoryRepository

class RemoteNativeStoryRepositoryImpl(
    private val api: NativeStoryApi
): RemoteNativeStoryRepository {

    override suspend fun fetchNativeStory(tagetLang: String, collection: String, nativeLang: String): Map<Int, Map<Int, Map<Int, NativeStory>>> {
        val dto = api.getNativeStories(tagetLang, collection, nativeLang)
        return dto.mapValues { (_, inner) ->
            inner.mapValues { (_, list) ->
                list.mapValues { (_, storyDto) ->
                    storyDto.toNativeStory(nativeLang)
                }
            }
        }
    }

    override suspend fun getNativeStoryVersion(tagetLang: String, collection: String, nativeLang: String): Double {
        return api.getNativeStoriesVersion(tagetLang, collection, nativeLang)
    }
}