package uz.alien.dictup.presentation.common.extention

import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import uz.alien.dictup.utils.Logger
import java.util.WeakHashMap

private val interstitialMap = WeakHashMap<AppCompatActivity, InterstitialAd?>()

var AppCompatActivity.interstitialAd: InterstitialAd?
    get() = interstitialMap[this]
    set(value) {
        interstitialMap[this] = value
    }

fun AppCompatActivity.loadInterstitialAd() {
    InterstitialAd.load(
        this,
        "ca-app-pub-7031957988362944/5271395557",
        AdRequest.Builder().build(),
        object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                Logger.d("Admob ads", "Ad was loaded.")
                interstitialAd = ad

                interstitialAd?.fullScreenContentCallback =
                    object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            // Called when fullscreen content is dismissed.
                            Logger.d("Admob ads", "Ad was dismissed.")
                            // Don't forget to set the ad reference to null so you
                            // don't show the ad a second time.
                            interstitialAd = null
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            // Called when fullscreen content failed to show.
                            Logger.d("Admob ads", "Ad failed to show.")
                            // Don't forget to set the ad reference to null so you
                            // don't show the ad a second time.
                            interstitialAd = null
                        }

                        override fun onAdShowedFullScreenContent() {
                            // Called when fullscreen content is shown.
                            Logger.d("Admob ads", "Ad showed fullscreen content.")
                        }

                        override fun onAdImpression() {
                            // Called when an impression is recorded for an ad.
                            Logger.d("Admob ads", "Ad recorded an impression.")
                        }

                        override fun onAdClicked() {
                            // Called when ad is clicked.
                            Logger.d("Admob ads", "Ad was clicked.")
                        }
                    }
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Logger.d("Admob ads", adError.message)
                interstitialAd = null
            }
        },
    )
}