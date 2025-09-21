package uz.alien.dictup.domain.repository

interface HttpManager {
    suspend fun get(url: String): Result<String>
    suspend fun getBytes(url: String): Result<ByteArray>
}
