package uz.alien.dictup.archive.manager

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReferralManager {
  fun fetchReferral(context: Context) {
    val referrerClient: InstallReferrerClient = InstallReferrerClient.newBuilder(context).build()

    referrerClient.startConnection(object : InstallReferrerStateListener {
      override fun onInstallReferrerSetupFinished(responseCode: Int) {
        when (responseCode) {
          InstallReferrerClient.InstallReferrerResponse.OK -> try {
            val response: ReferrerDetails = referrerClient.installReferrer
            val referrerUrl: String = response.installReferrer // referral_code_123
            val clickTime: Long = response.referrerClickTimestampSeconds
            val installTime: Long = response.installBeginTimestampSeconds

            Log.d("ReferralManager", "Referral: $referrerUrl")
            saveReferralToStorage(context, referrerUrl, clickTime, installTime)
          } catch (e: Exception) {
            Log.e("ReferralManager", "Error reading referral: ", e)
          }

          InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> Log.e(
            "ReferralManager",
            "Referrer not supported"
          )

          InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> Log.e(
            "ReferralManager",
            "Service unavailable"
          )
        }
      }

      override fun onInstallReferrerServiceDisconnected() {
        Log.d("ReferralManager", "Service disconnected")
      }
    })
  }

  fun saveReferralToStorage(context: Context, referrer: String, clickTime: Long, installTime: Long) {
    try {
      val dir = File(context.getExternalFilesDir(null), "data")
      if (!dir.exists()) {
        dir.mkdirs()
      }

      val file = File(dir, "referral_info.txt")
      val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
      val data = """
            Time: $timestamp
            Referrer: $referrer
            Click Time: $clickTime
            Install Time: $installTime
            
        """.trimIndent()

      file.appendText(data)
      Log.d("ReferralManager", "Referral saved to storage: ${file.absolutePath}")
    } catch (e: Exception) {
      Log.e("ReferralManager", "Error saving referral to storage", e)
    }
  }

  fun getDeviceId(context: Context): String {
    return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
  }

  fun generateReferralLink(context: Context): String {
    val deviceId = getDeviceId(context)
    val baseUrl = "https://play.google.com/store/apps/details"
    val packageName = context.packageName
    val referralLink = "$baseUrl?id=$packageName&referrer=$deviceId"
    return referralLink
  }

  fun shareReferralLink(context: Context) {
    val referralLink = generateReferralLink(context)
    val shareIntent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TEXT, referralLink)
      type = "text/plain"
    }
    context.startActivity(Intent.createChooser(shareIntent, "Do‘stlarga ulashish"))
  }
}