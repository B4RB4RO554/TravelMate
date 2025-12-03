package tn.bidpaifusion.travelmatekotlin.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import tn.bidpaifusion.travelmatekotlin.data.local.NotificationPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val notificationPrefs = remember { NotificationPreferences(context) }
    val scrollState = rememberScrollState()

    // State for notification preferences
    var notificationsEnabled by remember { mutableStateOf(notificationPrefs.notificationsEnabled) }
    var flightAlerts by remember { mutableStateOf(notificationPrefs.flightAlertsEnabled) }
    var weatherAlerts by remember { mutableStateOf(notificationPrefs.weatherAlertsEnabled) }
    var bookingReminders by remember { mutableStateOf(notificationPrefs.bookingRemindersEnabled) }
    var tripReminders by remember { mutableStateOf(notificationPrefs.tripRemindersEnabled) }
    var quietHoursEnabled by remember { mutableStateOf(notificationPrefs.quietHoursEnabled) }
    var quietHoursStart by remember { mutableStateOf(notificationPrefs.quietHoursStart) }
    var quietHoursEnd by remember { mutableStateOf(notificationPrefs.quietHoursEnd) }
    var reminderTime by remember { mutableStateOf(notificationPrefs.reminderTimeHours) }

    // Permission handling for Android 13+
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (!isGranted) {
            notificationsEnabled = false
            notificationPrefs.notificationsEnabled = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notification Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
            // Permission warning
            if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Permission Required",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                "Allow notifications to receive travel alerts",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        TextButton(
                            onClick = {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        ) {
                            Text("Grant")
                        }
                    }
                }
            }

            // Master Toggle
            SettingsSection(title = "General") {
                SettingsSwitch(
                    title = "Enable Notifications",
                    subtitle = "Receive travel alerts and reminders",
                    icon = Icons.Default.Notifications,
                    checked = notificationsEnabled,
                    enabled = hasNotificationPermission,
                    onCheckedChange = { enabled ->
                        notificationsEnabled = enabled
                        notificationPrefs.notificationsEnabled = enabled
                        
                        // Start/stop workers based on toggle
                        if (enabled) {
                        } else {
                        }
                    }
                )
            }

            // Notification Types
            SettingsSection(title = "Notification Types") {
                SettingsSwitch(
                    title = "Flight Alerts",
                    subtitle = "Get notified about flight delays and gate changes",
                    icon = Icons.Default.Flight,
                    checked = flightAlerts,
                    enabled = notificationsEnabled,
                    onCheckedChange = {
                        flightAlerts = it
                        notificationPrefs.flightAlertsEnabled = it
                    }
                )

                Divider(modifier = Modifier.padding(start = 72.dp))

                SettingsSwitch(
                    title = "Weather Alerts",
                    subtitle = "Receive alerts for extreme weather at your destinations",
                    icon = Icons.Default.WbCloudy,
                    checked = weatherAlerts,
                    enabled = notificationsEnabled,
                    onCheckedChange = {
                        weatherAlerts = it
                        notificationPrefs.weatherAlertsEnabled = it
                    }
                )

                Divider(modifier = Modifier.padding(start = 72.dp))

                SettingsSwitch(
                    title = "Booking Reminders",
                    subtitle = "Reminders for scheduled activities and reservations",
                    icon = Icons.Default.Event,
                    checked = bookingReminders,
                    enabled = notificationsEnabled,
                    onCheckedChange = {
                        bookingReminders = it
                        notificationPrefs.bookingRemindersEnabled = it
                    }
                )

                Divider(modifier = Modifier.padding(start = 72.dp))

                SettingsSwitch(
                    title = "Trip Reminders",
                    subtitle = "Get reminded about upcoming trips",
                    icon = Icons.Default.Luggage,
                    checked = tripReminders,
                    enabled = notificationsEnabled,
                    onCheckedChange = {
                        tripReminders = it
                        notificationPrefs.tripRemindersEnabled = it
                    }
                )
            }

            // Reminder Timing
            SettingsSection(title = "Reminder Timing") {
                var expanded by remember { mutableStateOf(false) }
                
                ListItem(
                    headlineText = { Text("Remind me") },
                    supportingText = {
                        Text("$reminderTime hour${if (reminderTime > 1) "s" else ""} before event")
                    },
                    leadingContent = {
                        Icon(Icons.Default.AccessTime, contentDescription = null)
                    },
                    trailingContent = {
                        Box {
                            TextButton(onClick = { expanded = true }) {
                                Text("Change")
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                listOf(1, 2, 3, 6, 12, 24).forEach { hours ->
                                    DropdownMenuItem(
                                        text = { Text("$hours hour${if (hours > 1) "s" else ""}") },
                                        onClick = {
                                            reminderTime = hours
                                            notificationPrefs.reminderTimeHours = hours
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                )
            }

            // Quiet Hours
            SettingsSection(title = "Quiet Hours") {
                SettingsSwitch(
                    title = "Enable Quiet Hours",
                    subtitle = "Mute notifications during specific times",
                    icon = Icons.Default.DoNotDisturb,
                    checked = quietHoursEnabled,
                    enabled = notificationsEnabled,
                    onCheckedChange = {
                        quietHoursEnabled = it
                        notificationPrefs.quietHoursEnabled = it
                    }
                )

                if (quietHoursEnabled) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("From", style = MaterialTheme.typography.labelMedium)
                            TimePicker(
                                hour = quietHoursStart,
                                onHourChange = {
                                    quietHoursStart = it
                                    notificationPrefs.quietHoursStart = it
                                }
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("To", style = MaterialTheme.typography.labelMedium)
                            TimePicker(
                                hour = quietHoursEnd,
                                onHourChange = {
                                    quietHoursEnd = it
                                    notificationPrefs.quietHoursEnd = it
                                }
                            )
                        }
                    }
                }
            }

            // Test Notification
            SettingsSection(title = "Testing") {
                Button(
                    onClick = {
                        val notificationHelper = tn.bidpaifusion.travelmatekotlin.TravelMateApplication.getNotificationHelper()
                        notificationHelper.showTripReminder(
                            tripId = "test",
                            destination = "Paris",
                            daysUntilTrip = 1
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = notificationsEnabled && hasNotificationPermission
                ) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Send Test Notification")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column {
                content()
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun SettingsSwitch(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineText = {
            Text(
                title,
                color = if (enabled) MaterialTheme.colorScheme.onSurface
                       else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        },
        supportingText = {
            Text(
                subtitle,
                color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant
                       else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        },
        leadingContent = {
            Icon(
                icon,
                contentDescription = null,
                tint = if (enabled) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    )
}

@Composable
private fun TimePicker(
    hour: Int,
    onHourChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    OutlinedButton(onClick = { expanded = true }) {
        Text(String.format("%02d:00", hour))
    }
    
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        (0..23).forEach { h ->
            DropdownMenuItem(
                text = { Text(String.format("%02d:00", h)) },
                onClick = {
                    onHourChange(h)
                    expanded = false
                }
            )
        }
    }
}
