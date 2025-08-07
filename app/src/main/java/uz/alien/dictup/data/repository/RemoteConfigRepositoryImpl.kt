package uz.alien.dictup.data.repository

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.tasks.await
import uz.alien.dictup.domain.repository.RemoteConfigRepository

class RemoteConfigRepositoryImpl(
    private val remoteConfig: FirebaseRemoteConfig
) : RemoteConfigRepository {

    override suspend fun fetchAndActivate(): Boolean {
        return try {
            remoteConfig.fetchAndActivate().await()
        } catch (_: Exception) {
            false
        }
    }

    override fun getString(key: String): String {
        return remoteConfig.getString(key)
    }

    override fun getBoolean(key: String): Boolean {
        return remoteConfig.getBoolean(key)
    }

    override fun getLong(key: String): Long {
        return remoteConfig.getLong(key)
    }

    override fun isAdsAvailable(): Boolean {
        return getBoolean("ads_available")
    }

    override fun getAssetsVersion(): Long {
        return getLong("assets_version")
    }

    override fun getDeveloper(): String {
        return getString("developer")
    }

    override fun getDeveloperContact(): String {
        return getString("developer_contact")
    }

    override fun getFrequencyOfAds(): Long {
        return getLong("frequency_of_ads")
    }

    override fun getInstagramLink(): String {
        return getString("instagram")
    }

    override fun isReferralAvailable(): Boolean {
        return getBoolean("referral_available")
    }

    override fun getTelegramLink(): String {
        return getString("telegram")
    }

    override fun getWebsite(): String {
        return getString("website")
    }
}