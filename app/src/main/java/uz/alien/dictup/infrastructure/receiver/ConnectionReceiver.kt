package uz.alien.dictup.infrastructure.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.alien.dictup.utils.Logger
import uz.alien.dictup.infrastructure.shared.InternetSyncEntryPoint
import uz.alien.dictup.infrastructure.worker.SyncWorker

class ConnectionReceiver : BroadcastReceiver() {

    private val workerName = "internet_sync_work"

    fun scheduleSyncWork(context: Context) {

        val workRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            workerName,
            ExistingWorkPolicy.KEEP,
            workRequest
        )
    }

    override fun onReceive(context: Context, intent: Intent?) {

        CoroutineScope(Dispatchers.IO).launch {
            if (isConnected(context)) {
                scheduleSyncWork(context)
            } else {
                WorkManager.getInstance(context).cancelUniqueWork(workerName)
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