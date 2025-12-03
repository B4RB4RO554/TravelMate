package tn.bidpaifusion.travelmatekotlin

import android.app.Application
import android.os.Build
import androidx.work.*
import tn.bidpaifusion.travelmatekotlin.notifications.NotificationHelper
import tn.bidpaifusion.travelmatekotlin.workers.SyncWorker
import java.util.concurrent.TimeUnit

class TravelMateApplication : Application() {

    lateinit var notificationHelper: NotificationHelper
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // Initialize notifications
        initializeNotifications()
        
        // Initialize periodic background sync
        setupPeriodicSync()
        
        // Schedule notification workers
        scheduleNotificationWorkers()
    }

    private fun initializeNotifications() {
        notificationHelper = NotificationHelper(this)

        // Note: Notification channels are created automatically by NotificationHelper
        // when it's initialized
    }

    private fun setupPeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicSyncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            SyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicSyncRequest
        )
    }

    private fun scheduleNotificationWorkers() {
        // TODO: Fix notification workers compilation issues
        // Temporarily disabled to get working build
    }

    fun triggerManualSync(token: String, userId: String) {
        val data = workDataOf(
            "token" to token,
            "userId" to userId
        )

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setInputData(data)
            .build()

        WorkManager.getInstance(this).enqueue(syncRequest)
    }

    companion object {
        lateinit var instance: TravelMateApplication
            private set
        
        fun getNotificationHelper(): NotificationHelper = instance.notificationHelper
    }
}
