package uz.alien.dictup.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import uz.alien.dictup.domain.repository.HttpManager
import java.io.IOException

class OkHttpManagerImpl(
    private val client: OkHttpClient = OkHttpClient()
) : HttpManager {

    override suspend fun get(url: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(IOException("HTTP error ${response.code}"))
                }
                val body = response.body?.string()
                    ?: return@withContext Result.failure(IOException("Empty body"))
                Result.success(body)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBytes(url: String): Result<ByteArray> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(IOException("HTTP error ${response.code}"))
                }
                val body = response.body?.bytes()
                    ?: return@withContext Result.failure(IOException("Empty body"))
                Result.success(body)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}