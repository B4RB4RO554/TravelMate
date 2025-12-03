package tn.bidpaifusion.travelmatekotlin.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToNotifications: () -> Unit = {}
) {
    var offlineMode by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("English") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // App Settings Section
            Text(
                "App Settings",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    ListItem(
                        headlineText = { Text("Offline Mode") },
                        supportingText = { Text("Save data for offline access") },
                        leadingContent = { Icon(Icons.Default.Cloud, null) },
                        trailingContent = {
                            Switch(
                                checked = offlineMode,
                                onCheckedChange = { offlineMode = it }
                            )
                        }
                    )
                    Divider()
                    ListItem(
                        headlineText = { Text("Notifications") },
                        supportingText = { Text("Flight alerts, weather, trip reminders") },
                        leadingContent = { Icon(Icons.Default.Notifications, null) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, null) },
                        modifier = Modifier.clickable { onNavigateToNotifications() }
                    )
                    Divider()
                    ListItem(
                        headlineText = { Text("Dark Mode") },
                        supportingText = { Text("Use dark theme") },
                        leadingContent = { Icon(Icons.Default.DarkMode, null) },
                        trailingContent = {
                            Switch(
                                checked = darkMode,
                                onCheckedChange = { darkMode = it }
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Language Section
            Text(
                "Language",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    val languages = listOf("English", "Français", "العربية", "Español")
                    languages.forEach { lang ->
                        ListItem(
                            headlineText = { Text(lang) },
                            leadingContent = {
                                RadioButton(
                                    selected = selectedLanguage == lang,
                                    onClick = { selectedLanguage = lang }
                                )
                            },
                            modifier = Modifier.padding(vertical = 0.dp)
                        )
                        if (lang != languages.last()) {
                            Divider()
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // About Section
            Text(
                "About",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    ListItem(
                        headlineText = { Text("Version") },
                        supportingText = { Text("1.0.0") },
                        leadingContent = { Icon(Icons.Default.Info, null) }
                    )
                    Divider()
                    ListItem(
                        headlineText = { Text("Help & Support") },
                        leadingContent = { Icon(Icons.Default.HelpOutline, null) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, null) }
                    )
                    Divider()
                    ListItem(
                        headlineText = { Text("Privacy Policy") },
                        leadingContent = { Icon(Icons.Default.PrivacyTip, null) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, null) }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Logout Button
            Button(
                onClick = { /* Handle logout */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.ExitToApp, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout")
            }
        }
    }
}
