package uz.alien.test.words

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import uz.alien.test.words.WordsActivity.Companion.isConnected

class ConnectionReceiver(private val onConnected: () -> Unit) : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent?) {
    if (isConnected(context)) {
      onConnected()
      context.unregisterReceiver(this)
    }
  }

  companion object {
    fun register(context: Context, onConnected: () -> Unit) {
      val receiver = ConnectionReceiver(onConnected)
      val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
      context.registerReceiver(receiver, filter)
    }
  }
}