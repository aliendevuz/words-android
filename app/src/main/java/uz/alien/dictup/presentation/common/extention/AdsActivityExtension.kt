package uz.alien.dictup.presentation.common.extention

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import uz.alien.dictup.presentation.features.base.BaseActivity
import uz.alien.dictup.utils.Logger
import java.util.WeakHashMap

private val interstitialMap = WeakHashMap<Application, InterstitialAd?>()

var Application.interstitialAd: InterstitialAd?
    get() = interstitialMap[this]
    set(value) {
        interstitialMap[this] = value
    }

fun Application.loadInterstitialAd() {
    InterstitialAd.load(
        this,
        "ca-app-pub-7031957988362944/5271395557",
        AdRequest.Builder().build(),
        object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                Logger.d("Admob ads", "Ad was loaded.")
                interstitialAd = ad
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Logger.d("Admob ads", adError.message)
                interstitialAd = null
            }
        }
    )
}
