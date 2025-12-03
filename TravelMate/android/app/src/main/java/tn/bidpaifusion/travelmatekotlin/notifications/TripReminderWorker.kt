package tn.bidpaifusion.travelmatekotlin.notifications

import android.content.Context
import androidx.work.*
import tn.bidpaifusion.travelmatekotlin.data.local.AppDatabase
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * WorkManager worker that checks trips and schedules reminders
 */
class TripReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val database = AppDatabase.getDatabase(applicationContext)
            val tripDao = database.tripDao()
            val notificationHelper = NotificationHelper(applicationContext)

            // Get all trips
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val today = Calendar.getInstance()

            // Check each trip (simplified - in production use Flow)
            tripDao.getAllTrips().collect { trips ->
                for (trip in trips) {
                    try {
                        val tripDate = dateFormat.parse(trip.startDate) ?: continue
                        val tripCalendar = Calendar.getInstance().apply { time = tripDate }

                        val daysUntilTrip = getDaysDifference(today, tripCalendar)

                        // Send notifications at specific intervals
                        when (daysUntilTrip) {
                            0, 1, 3, 7 -> {
                                notificationHelper.showTripReminder(
                                    tripId = trip.id,
                                    destination = trip.destination,
                                    daysUntilTrip = daysUntilTrip
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun getDaysDifference(start: Calendar, end: Calendar): Int {
        val startMillis = start.timeInMillis
        val endMillis = end.timeInMillis
        val diffMillis = endMillis - startMillis
        return (diffMillis / (24 * 60 * 60 * 1000)).toInt()
    }

    companion object {
        const val WORK_NAME = "trip_reminder_worker"

        /**
         * Schedule daily check for trip reminders
         */
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()

            val dailyWorkRequest = PeriodicWorkRequestBuilder<TripReminderWorker>(
                24, TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                dailyWorkRequest
            )
        }

        /**
         * Calculate delay to run at 9 AM
         */
        private fun calculateInitialDelay(): Long {
            val now = Calendar.getInstance()
            val target = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                if (before(now)) {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            }
            return target.timeInMillis - now.timeInMillis
        }

        /**
         * Run immediately (for testing)
         */
        fun runNow(context: Context) {
            val workRequest = OneTimeWorkRequestBuilder<TripReminderWorker>()
                .build()
            WorkManager.getInstance(context).enqueue(workRequest)
        }

        /**
         * Cancel scheduled reminders
         */
        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
