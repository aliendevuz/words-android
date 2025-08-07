package uz.alien.dictup.domain.repository.retrofit

import uz.alien.dictup.domain.model.Story

interface RemoteStoryRepository {

    suspend fun fetchStory(targetLang: String, collection: String): Map<Int, Map<Int, Map<Int, Story>>>

    suspend fun getStoryVersion(targetLang: String, collection: String): Double
}