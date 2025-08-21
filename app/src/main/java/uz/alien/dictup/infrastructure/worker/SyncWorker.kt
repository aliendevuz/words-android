package uz.alien.dictup.infrastructure.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.hilt.android.EntryPointAccessors
import okio.IOException
import uz.alien.dictup.utils.Logger
import uz.alien.dictup.infrastructure.shared.InternetSyncEntryPoint

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val entryPoint: InternetSyncEntryPoint =
        EntryPointAccessors.fromApplication(appContext, InternetSyncEntryPoint::class.java)

    override suspend fun doWork(): Result {
        return try {
            entryPoint.syncDataUseCaseAndSetupScore().runAllSteps()
            Result.success()
        } catch (e: IOException) {
            Logger.e("SyncWorker", "IOError: ${e.message}")
            Result.retry()
        } catch (e: Exception) {
            Logger.e("SyncWorker", "Error: ${e.message}")
            Result.failure()
        }
    }
}