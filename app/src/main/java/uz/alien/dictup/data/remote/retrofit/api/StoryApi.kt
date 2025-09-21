package uz.alien.dictup.data.remote.retrofit.api

import retrofit2.http.GET
import retrofit2.http.Path
import uz.alien.dictup.data.remote.retrofit.dto.StoryDto

interface StoryApi {

    @GET("assets/{targetLang}/{collection}/b-u-stories.json")
    suspend fun getStories(
        @Path("targetLang") targetLang: String,
        @Path("collection") collection: String
    ): Map<Int, Map<Int, Map<Int, StoryDto>>>

    @GET("assets/.v/{targetLang}/{collection}/b-u-stories.json")
    suspend fun getStoriesVersion(
        @Path("targetLang") targetLang: String,
        @Path("collection") collection: String
    ): Double
}