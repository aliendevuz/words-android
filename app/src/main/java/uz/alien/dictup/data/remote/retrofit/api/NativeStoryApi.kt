package uz.alien.dictup.data.remote.retrofit.api

import retrofit2.http.GET
import retrofit2.http.Path
import uz.alien.dictup.data.remote.retrofit.dto.StoryDto

interface NativeStoryApi {

    @GET("assets/{targetLang}/{collection}/{nativeLang}/stories.json")
    suspend fun getNativeStories(
        @Path("targetLang") targetLang: String,
        @Path("collection") collection: String,
        @Path("nativeLang") nativeLang: String
    ): Map<Int, Map<Int, Map<Int, StoryDto>>>

    @GET("assets/.v/{targetLang}/{collection}/{nativeLang}/stories.json")
    suspend fun getNativeStoriesVersion(
        @Path("targetLang") targetLang: String,
        @Path("collection") collection: String,
        @Path("nativeLang") nativeLang: String
    ): Double
}