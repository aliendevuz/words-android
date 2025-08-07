package uz.alien.dictup.data.repository.retrofit

import uz.alien.dictup.data.mapper.toStory
import uz.alien.dictup.data.remote.retrofit.api.StoryApi
import uz.alien.dictup.domain.model.Story
import uz.alien.dictup.domain.repository.retrofit.RemoteStoryRepository

class RemoteStoryRepositoryImpl(
    private val api: StoryApi
) : RemoteStoryRepository {

    override suspend fun fetchStory(targetLang: String, collection: String): Map<Int, Map<Int, Map<Int, Story>>> {
        val dto = api.getStories(targetLang, collection)
        return dto.mapValues { (_, inner) ->
            inner.mapValues { (_, list) ->
                list.mapValues { (_, storyDto) ->
                    storyDto.toStory()
                }
            }
        }
    }

    override suspend fun getStoryVersion(targetLang: String, collection: String): Double {
        return api.getStoriesVersion(targetLang, collection)
    }
}