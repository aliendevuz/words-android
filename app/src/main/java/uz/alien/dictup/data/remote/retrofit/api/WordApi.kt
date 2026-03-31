package uz.alien.dictup.data.remote.retrofit.api

import retrofit2.http.GET
import retrofit2.http.Path
import uz.alien.dictup.data.remote.retrofit.dto.WordDto

interface WordApi {

    @GET("{targetLang}/{collection}/b-u-words.json")
    suspend fun getWords(
        @Path("targetLang") targetLang: String,
        @Path("collection") collection: String
    ): Map<Int, Map<Int, List<WordDto>>>

    @GET(".v/{targetLang}/{collection}/b-u-words.json")
    suspend fun getWordsVersion(
        @Path("targetLang") targetLang: String,
        @Path("collection") collection: String
    ): Double

//    @GET(".hash/{targetLang}/{collection}/b-u-words.json.sha256")
//    suspend fun getWordsHash(
//        @Path("targetLang") targetLang: String,
//        @Path("collection") collection: String
//    ): String
}