package uz.alien.dictup

import android.app.Application
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class App : Application() {

  override fun onCreate() {
    super.onCreate()
    CoroutineScope(Dispatchers.IO).launch {
      MobileAds.initialize(this@App)
    }
  }
}
