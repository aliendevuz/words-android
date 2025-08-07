package uz.alien.dictup

import android.app.Application
import android.content.IntentFilter
import android.net.ConnectivityManager
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import uz.alien.dictup.infrastructure.receiver.ConnectionReceiver

@HiltAndroidApp
class App : Application() {

  private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

  override fun onCreate() {
    super.onCreate()
    val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
    val receiver = ConnectionReceiver()
    applicationContext.registerReceiver(receiver, filter)
    appScope.launch {
      MobileAds.initialize(this@App)
    }
  }
}