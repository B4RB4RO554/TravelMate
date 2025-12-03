package tn.bidpaifusion.travelmatekotlin.data.repository

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import tn.bidpaifusion.travelmatekotlin.workers.SyncWorker
import java.util.concurrent.TimeUnit

/**
 * Manages data synchronization between local database and remote server.
 * Handles both manual sync and background periodic sync.
 */
class SyncManager(private val context: Context) {
    
    private val workManager = WorkManager.getInstance(context)
    
    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState
    
    /**
     * Start a manual sync immediately
     */
    fun syncNow() {
        _syncState.value = SyncState.Syncing
        
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .addTag("manual_sync")
            .build()
        
        workManager.enqueueUniqueWork(
            "manual_sync",
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
        
        // Observe the work status
        workManager.getWorkInfoByIdLiveData(syncRequest.id).observeForever { workInfo ->
            when (workInfo?.state) {
                WorkInfo.State.SUCCEEDED -> {
                    _syncState.value = SyncState.Success(
                        workInfo.outputData.getInt("synced_count", 0)
                    )
                }
                WorkInfo.State.FAILED -> {
                    _syncState.value = SyncState.Error(
                        workInfo.outputData.getString("error") ?: "Sync failed"
                    )
                }
                WorkInfo.State.RUNNING -> {
                    _syncState.value = SyncState.Syncing
                }
                else -> {}
            }
        }
    }
    
    /**
     * Schedule periodic background sync (every 15 minutes when connected)
     */
    fun schedulePeriodicSync() {
        val periodicSync = PeriodicWorkRequestBuilder<SyncWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .addTag("periodic_sync")
            .build()
        
        workManager.enqueueUniquePeriodicWork(
            "periodic_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicSync
        )
    }
    
    /**
     * Cancel all scheduled syncs
     */
    fun cancelPeriodicSync() {
        workManager.cancelUniqueWork("periodic_sync")
    }
    
    /**
     * Reset sync state to idle
     */
    fun resetState() {
        _syncState.value = SyncState.Idle
    }
}

sealed class SyncState {
    object Idle : SyncState()
    object Syncing : SyncState()
    data class Success(val syncedCount: Int) : SyncState()
    data class Error(val message: String) : SyncState()
}
