package uz.alien.dictup.domain.repository

interface RemoteConfigRepository {

    suspend fun fetchAndActivate(): Boolean

    fun getString(key: String): String

    fun getBoolean(key: String): Boolean

    fun getLong(key: String): Long


    fun isAdsAvailable(): Boolean

    fun getAssetsVersion(): Long

    fun getDeveloper(): String

    fun getDeveloperContact(): String

    fun getFrequencyOfAds(): Long

    fun getInstagramLink(): String

    fun isReferralAvailable(): Boolean

    fun getTelegramLink(): String

    fun getWebsite(): String
}