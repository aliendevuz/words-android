package uz.alien.dictup.infrastructure.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.alien.dictup.di.InternetSyncEntryPoint

class ConnectionReceiver() : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent?) {
    if (isConnected(context)) {
      CoroutineScope(Dispatchers.IO).launch {

        val entryPoint = EntryPointAccessors.fromApplication(
          context.applicationContext,
          InternetSyncEntryPoint::class.java
        )

        entryPoint.onInternetConnectedUseCases().syncDataUseCaseAndSetupScore()
      }
    }
  }

  companion object {

    fun isConnected(context: Context): Boolean {
      val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
      val nw = cm.activeNetwork ?: return false
      val caps = cm.getNetworkCapabilities(nw) ?: return false
      return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
  }
}