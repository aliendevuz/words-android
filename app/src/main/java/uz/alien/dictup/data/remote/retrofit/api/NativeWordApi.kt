package uz.alien.dictup.data.remote.retrofit.api

import retrofit2.http.GET
import retrofit2.http.Path
import uz.alien.dictup.data.remote.retrofit.dto.WordDto

interface NativeWordApi {

    @GET("assets/{targetLang}/{collection}/{nativeLang}/words.json")
    suspend fun getNativeWords(
        @Path("targetLang") targetLang: String,
        @Path("collection") collection: String,
        @Path("nativeLang") nativeLang: String
    ): Map<Int, Map<Int, List<WordDto>>>

    @GET("assets/.v/{targetLang}/{collection}/{nativeLang}/words.json")
    suspend fun getNativeWordsVersion(
        @Path("targetLang") targetLang: String,
        @Path("collection") collection: String,
        @Path("nativeLang") nativeLang: String
    ): Double
}