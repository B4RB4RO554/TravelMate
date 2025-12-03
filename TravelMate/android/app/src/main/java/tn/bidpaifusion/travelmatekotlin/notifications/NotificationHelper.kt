package tn.bidpaifusion.travelmatekotlin.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import tn.bidpaifusion.travelmatekotlin.MainActivity
import tn.bidpaifusion.travelmatekotlin.R

/**
 * Helper class to manage all app notifications
 * Handles: Trip reminders, Weather alerts, Flight changes, Booking reminders
 */
class NotificationHelper(private val context: Context) {

    companion object {
        // Notification Channel IDs
        const val CHANNEL_TRIP_REMINDERS = "trip_reminders"
        const val CHANNEL_WEATHER_ALERTS = "weather_alerts"
        const val CHANNEL_FLIGHT_CHANGES = "flight_changes"
        const val CHANNEL_BOOKING_REMINDERS = "booking_reminders"
        const val CHANNEL_EMERGENCY = "emergency_alerts"

        // Notification IDs
        const val NOTIFICATION_TRIP_REMINDER = 1001
        const val NOTIFICATION_WEATHER_ALERT = 2001
        const val NOTIFICATION_FLIGHT_CHANGE = 3001
        const val NOTIFICATION_BOOKING_REMINDER = 4001
        const val NOTIFICATION_EMERGENCY = 5001
    }

    init {
        createNotificationChannels()
    }

    /**
     * Create all notification channels (required for Android 8.0+)
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(NotificationManager::class.java)

            // Trip Reminders Channel
            val tripChannel = NotificationChannel(
                CHANNEL_TRIP_REMINDERS,
                "Trip Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders about upcoming trips"
                enableVibration(true)
            }

            // Weather Alerts Channel
            val weatherChannel = NotificationChannel(
                CHANNEL_WEATHER_ALERTS,
                "Weather Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Weather alerts for your destinations"
                enableVibration(true)
            }

            // Flight Changes Channel
            val flightChannel = NotificationChannel(
                CHANNEL_FLIGHT_CHANGES,
                "Flight Updates",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Important flight status changes"
                enableVibration(true)
            }

            // Booking Reminders Channel
            val bookingChannel = NotificationChannel(
                CHANNEL_BOOKING_REMINDERS,
                "Booking Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders for reservations and bookings"
            }

            // Emergency Channel
            val emergencyChannel = NotificationChannel(
                CHANNEL_EMERGENCY,
                "Emergency Alerts",
                NotificationManager.IMPORTANCE_MAX
            ).apply {
                description = "Critical emergency notifications"
                enableVibration(true)
                enableLights(true)
            }

            notificationManager.createNotificationChannels(
                listOf(tripChannel, weatherChannel, flightChannel, bookingChannel, emergencyChannel)
            )
        }
    }

    /**
     * Show trip reminder notification
     */
    fun showTripReminder(
        tripId: String,
        destination: String,
        daysUntilTrip: Int,
        notificationId: Int = NOTIFICATION_TRIP_REMINDER
    ) {
        val title = when (daysUntilTrip) {
            0 -> "🎉 Your trip starts today!"
            1 -> "✈️ Trip tomorrow!"
            else -> "📅 Trip in $daysUntilTrip days"
        }

        val message = when (daysUntilTrip) {
            0 -> "Your trip to $destination begins today. Have a great journey!"
            1 -> "Your trip to $destination is tomorrow. Don't forget to pack!"
            7 -> "Your trip to $destination is in one week. Time to prepare!"
            else -> "Your trip to $destination is coming up in $daysUntilTrip days."
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("trip_id", tripId)
            putExtra("navigate_to", "trip_details")
        }

        val pendingIntent = PendingIntent.getActivity(
            context, notificationId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_TRIP_REMINDERS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_launcher_foreground,
                "View Trip",
                pendingIntent
            )
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId + tripId.hashCode(), notification)
        } catch (e: SecurityException) {
            // Handle case where notification permission not granted
            e.printStackTrace()
        }
    }

    /**
     * Show weather alert notification
     */
    fun showWeatherAlert(
        destination: String,
        weatherCondition: String,
        temperature: String,
        alertType: WeatherAlertType = WeatherAlertType.INFO
    ) {
        val (title, icon) = when (alertType) {
            WeatherAlertType.SEVERE -> "⚠️ Severe Weather Alert" to "🌪️"
            WeatherAlertType.WARNING -> "🌧️ Weather Warning" to "⛈️"
            WeatherAlertType.INFO -> "🌤️ Weather Update" to "☀️"
        }

        val message = "$icon $destination: $weatherCondition, $temperature"

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "weather")
        }

        val pendingIntent = PendingIntent.getActivity(
            context, NOTIFICATION_WEATHER_ALERT, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val priority = when (alertType) {
            WeatherAlertType.SEVERE -> NotificationCompat.PRIORITY_MAX
            WeatherAlertType.WARNING -> NotificationCompat.PRIORITY_HIGH
            WeatherAlertType.INFO -> NotificationCompat.PRIORITY_DEFAULT
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_WEATHER_ALERTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(priority)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(
                NOTIFICATION_WEATHER_ALERT + destination.hashCode(), 
                notification
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    /**
     * Show flight change notification
     */
    fun showFlightChangeAlert(
        flightNumber: String,
        changeType: FlightChangeType,
        details: String
    ) {
        val (title, emoji) = when (changeType) {
            FlightChangeType.DELAYED -> "⏰ Flight Delayed" to "⏰"
            FlightChangeType.CANCELLED -> "❌ Flight Cancelled" to "❌"
            FlightChangeType.GATE_CHANGE -> "🚪 Gate Changed" to "🚪"
            FlightChangeType.TIME_CHANGE -> "🕐 Time Changed" to "🕐"
            FlightChangeType.ON_TIME -> "✅ Flight On Time" to "✅"
        }

        val message = "$emoji Flight $flightNumber: $details"

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "flights")
            putExtra("flight_number", flightNumber)
        }

        val pendingIntent = PendingIntent.getActivity(
            context, NOTIFICATION_FLIGHT_CHANGE, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val priority = when (changeType) {
            FlightChangeType.CANCELLED -> NotificationCompat.PRIORITY_MAX
            FlightChangeType.DELAYED, FlightChangeType.GATE_CHANGE -> NotificationCompat.PRIORITY_HIGH
            else -> NotificationCompat.PRIORITY_DEFAULT
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_FLIGHT_CHANGES)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(priority)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(
                NOTIFICATION_FLIGHT_CHANGE + flightNumber.hashCode(), 
                notification
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    /**
     * Show booking reminder notification
     */
    fun showBookingReminder(
        bookingType: String, // "Hotel", "Restaurant", "Activity"
        bookingName: String,
        dateTime: String,
        location: String
    ) {
        val emoji = when (bookingType.lowercase()) {
            "hotel" -> "🏨"
            "restaurant" -> "🍽️"
            "activity" -> "🎯"
            "flight" -> "✈️"
            else -> "📋"
        }

        val title = "$emoji $bookingType Reminder"
        val message = "$bookingName\n📍 $location\n🕐 $dateTime"

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "bookings")
        }

        val pendingIntent = PendingIntent.getActivity(
            context, NOTIFICATION_BOOKING_REMINDER, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_BOOKING_REMINDERS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText("$bookingName at $dateTime")
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(
                NOTIFICATION_BOOKING_REMINDER + bookingName.hashCode(), 
                notification
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    /**
     * Show emergency notification (highest priority)
     */
    fun showEmergencyNotification(
        title: String,
        message: String,
        actionText: String = "View Details"
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "emergency")
        }

        val pendingIntent = PendingIntent.getActivity(
            context, NOTIFICATION_EMERGENCY, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_EMERGENCY)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🚨 $title")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .addAction(
                R.drawable.ic_launcher_foreground,
                actionText,
                pendingIntent
            )
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_EMERGENCY, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    /**
     * Cancel a specific notification
     */
    fun cancelNotification(notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }

    /**
     * Cancel all notifications
     */
    fun cancelAllNotifications() {
        NotificationManagerCompat.from(context).cancelAll()
    }
}

enum class WeatherAlertType {
    SEVERE, WARNING, INFO
}

enum class FlightChangeType {
    DELAYED, CANCELLED, GATE_CHANGE, TIME_CHANGE, ON_TIME
}
