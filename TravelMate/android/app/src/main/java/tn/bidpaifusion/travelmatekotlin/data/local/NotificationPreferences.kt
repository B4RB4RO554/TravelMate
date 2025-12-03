package tn.bidpaifusion.travelmatekotlin.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages notification preferences using SharedPreferences
 */
class NotificationPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PREFS_NAME = "notification_preferences"
        
        // Keys
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_FLIGHT_ALERTS = "flight_alerts"
        private const val KEY_WEATHER_ALERTS = "weather_alerts"
        private const val KEY_BOOKING_REMINDERS = "booking_reminders"
        private const val KEY_TRIP_REMINDERS = "trip_reminders"
        private const val KEY_REMINDER_TIME = "reminder_time" // Hours before
        private const val KEY_QUIET_HOURS_START = "quiet_hours_start"
        private const val KEY_QUIET_HOURS_END = "quiet_hours_end"
        private const val KEY_QUIET_HOURS_ENABLED = "quiet_hours_enabled"
    }

    // Master notification toggle
    var notificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, value).apply()

    // Individual channel toggles
    var flightAlertsEnabled: Boolean
        get() = prefs.getBoolean(KEY_FLIGHT_ALERTS, true)
        set(value) = prefs.edit().putBoolean(KEY_FLIGHT_ALERTS, value).apply()

    var weatherAlertsEnabled: Boolean
        get() = prefs.getBoolean(KEY_WEATHER_ALERTS, true)
        set(value) = prefs.edit().putBoolean(KEY_WEATHER_ALERTS, value).apply()

    var bookingRemindersEnabled: Boolean
        get() = prefs.getBoolean(KEY_BOOKING_REMINDERS, true)
        set(value) = prefs.edit().putBoolean(KEY_BOOKING_REMINDERS, value).apply()

    var tripRemindersEnabled: Boolean
        get() = prefs.getBoolean(KEY_TRIP_REMINDERS, true)
        set(value) = prefs.edit().putBoolean(KEY_TRIP_REMINDERS, value).apply()

    // Reminder time (hours before event)
    var reminderTimeHours: Int
        get() = prefs.getInt(KEY_REMINDER_TIME, 1)
        set(value) = prefs.edit().putInt(KEY_REMINDER_TIME, value).apply()

    // Quiet hours
    var quietHoursEnabled: Boolean
        get() = prefs.getBoolean(KEY_QUIET_HOURS_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_QUIET_HOURS_ENABLED, value).apply()

    var quietHoursStart: Int // Hour of day (0-23)
        get() = prefs.getInt(KEY_QUIET_HOURS_START, 22)
        set(value) = prefs.edit().putInt(KEY_QUIET_HOURS_START, value).apply()

    var quietHoursEnd: Int // Hour of day (0-23)
        get() = prefs.getInt(KEY_QUIET_HOURS_END, 7)
        set(value) = prefs.edit().putInt(KEY_QUIET_HOURS_END, value).apply()

    /**
     * Check if notifications should be shown at current time
     */
    fun shouldShowNotification(): Boolean {
        if (!notificationsEnabled) return false
        if (!quietHoursEnabled) return true

        val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        
        return if (quietHoursStart <= quietHoursEnd) {
            // Normal range (e.g., 9-17)
            currentHour !in quietHoursStart..quietHoursEnd
        } else {
            // Overnight range (e.g., 22-7)
            currentHour < quietHoursStart && currentHour >= quietHoursEnd
        }
    }

    /**
     * Reset all preferences to defaults
     */
    fun resetToDefaults() {
        prefs.edit().clear().apply()
    }

    /**
     * Get all preferences as a map (for debugging/export)
     */
    fun getAllPreferences(): Map<String, Any> {
        return mapOf(
            "notificationsEnabled" to notificationsEnabled,
            "flightAlerts" to flightAlertsEnabled,
            "weatherAlerts" to weatherAlertsEnabled,
            "bookingReminders" to bookingRemindersEnabled,
            "tripReminders" to tripRemindersEnabled,
            "reminderTimeHours" to reminderTimeHours,
            "quietHoursEnabled" to quietHoursEnabled,
            "quietHoursStart" to quietHoursStart,
            "quietHoursEnd" to quietHoursEnd
        )
    }
}
