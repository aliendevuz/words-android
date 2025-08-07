package uz.alien.dictup.infrastructure.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.alien.dictup.domain.usecase.on_internet_connected.OnInternetConnectedUseCases
import javax.inject.Inject

//@AndroidEntryPoint
class ConnectionReceiver() : BroadcastReceiver() {

//  @Inject
//  private lateinit var onInternetConnectedUseCases: OnInternetConnectedUseCases

  override fun onReceive(context: Context, intent: Intent?) {
    if (isConnected(context)) {
      CoroutineScope(Dispatchers.IO).launch {
//        onInternetConnectedUseCases.syncDataUseCase()
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