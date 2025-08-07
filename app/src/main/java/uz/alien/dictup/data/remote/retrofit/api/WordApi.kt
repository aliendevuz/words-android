package uz.alien.dictup.data.remote.retrofit.api

import retrofit2.http.GET
import retrofit2.http.Path
import uz.alien.dictup.data.remote.retrofit.dto.WordDto

interface WordApi {

    @GET("assets/{targetLang}/{collection}/words.json")
    suspend fun getWords(
        @Path("targetLang") targetLang: String,
        @Path("collection") collection: String
    ): Map<Int, Map<Int, List<WordDto>>>

    @GET("assets/.v/{targetLang}/{collection}/words.json")
    suspend fun getWordsVersion(
        @Path("targetLang") targetLang: String,
        @Path("collection") collection: String
    ): Double
}