package uz.alien.dictup.archive.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class ConnectionReceiver(private val onConnected: () -> Unit) : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent?) {
    if (isConnected(context)) {
      onConnected()
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