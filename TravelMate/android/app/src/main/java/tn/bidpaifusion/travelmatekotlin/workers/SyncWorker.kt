package tn.bidpaifusion.travelmatekotlin.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tn.bidpaifusion.travelmatekotlin.data.repository.OfflineDataManager

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Get token from shared preferences or input data
            val sharedPrefs = applicationContext.getSharedPreferences("travelmate_prefs", Context.MODE_PRIVATE)
            val token = inputData.getString("token") 
                ?: sharedPrefs.getString("auth_token", null)
                ?: return@withContext Result.failure(workDataOf("error" to "No auth token"))

            val offlineManager = OfflineDataManager(applicationContext)
            
            // Sync pending trips
            val syncedCount = offlineManager.syncPendingTrips(token)
            
            Result.success(workDataOf("synced_count" to syncedCount))
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(workDataOf("error" to e.message))
        }
    }

    companion object {
        const val WORK_NAME = "sync_work"
    }
}
