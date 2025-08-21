package uz.alien.dictup.infrastructure.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import uz.alien.dictup.utils.Logger

class MyFirebaseInstanceIDService : FirebaseMessagingService() {

  override fun onNewToken(token: String) {
    super.onNewToken(token)
    Logger.d("NEW_TOKEN", token)
  }

  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    Logger.d(MyFirebaseInstanceIDService::class.java.simpleName, "From: ${remoteMessage.from}")

    if (remoteMessage.data.isNotEmpty()) {
      Logger.d(MyFirebaseInstanceIDService::class.java.simpleName, "Message data payload: ${remoteMessage.data}")
    }

    remoteMessage.notification?.let {
      Logger.d(MyFirebaseInstanceIDService::class.java.simpleName, "Message Notification Body: ${it.body}")
    }
  }
}