package uz.alien.dictup.data.repository

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.tasks.await
import uz.alien.dictup.domain.repository.RemoteConfigRepository
import uz.alien.dictup.value.strings.RemoteConfig.ADS_AVAILABLE
import uz.alien.dictup.value.strings.RemoteConfig.ASSETS_VERSION
import uz.alien.dictup.value.strings.RemoteConfig.DEVELOPER_CONTACT
import uz.alien.dictup.value.strings.RemoteConfig.DEVELOPER_NAME
import uz.alien.dictup.value.strings.RemoteConfig.FREQUENCY_OF_ADS
import uz.alien.dictup.value.strings.RemoteConfig.INSTAGRAM_LINK
import uz.alien.dictup.value.strings.RemoteConfig.IS_REFERRAL_AVAILABLE
import uz.alien.dictup.value.strings.RemoteConfig.TELEGRAM_LINK
import uz.alien.dictup.value.strings.RemoteConfig.WEBSITE_URL

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
        return getBoolean(ADS_AVAILABLE)
    }

    override fun getAssetsVersion(): Long {
        return getLong(ASSETS_VERSION)
    }

    override fun getDeveloper(): String {
        return getString(DEVELOPER_NAME)
    }

    override fun getDeveloperContact(): String {
        return getString(DEVELOPER_CONTACT)
    }

    override fun getFrequencyOfAds(): Long {
        return getLong(FREQUENCY_OF_ADS)
    }

    override fun getInstagramLink(): String {
        return getString(INSTAGRAM_LINK)
    }

    override fun isReferralAvailable(): Boolean {
        return getBoolean(IS_REFERRAL_AVAILABLE)
    }

    override fun getTelegramLink(): String {
        return getString(TELEGRAM_LINK)
    }

    override fun getWebsite(): String {
        return getString(WEBSITE_URL)
    }
}