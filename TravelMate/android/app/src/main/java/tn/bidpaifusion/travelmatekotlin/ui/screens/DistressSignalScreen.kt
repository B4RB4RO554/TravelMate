package tn.bidpaifusion.travelmatekotlin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import tn.bidpaifusion.travelmatekotlin.viewmodel.DistressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistressSignalScreen(
    onNavigateBack: () -> Unit,
    token: String,
    viewModel: DistressViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var message by remember { mutableStateOf("I need help!") }
    var showConfirmDialog by remember { mutableStateOf(false) }
    
    // Simulated location - in production use FusedLocationProviderClient
    val currentLat = 36.8065 // Tunis
    val currentLon = 10.1815

    LaunchedEffect(Unit) {
        viewModel.loadActiveSignals(token)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SOS Distress Signal") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Warning Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Emergency SOS",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "This will share your location with emergency contacts and authorities",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Active Signal Status
            if (state.activeSignal != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFCDD2)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFFC62828)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Active Distress Signal",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC62828)
                            )
                        }
                        Text(
                            text = "Message: ${state.activeSignal!!.message}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Location: ${state.activeSignal!!.latitude}, ${state.activeSignal!!.longitude}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        
                        Button(
                            onClick = {
                                viewModel.deactivateSignal(token, state.activeSignal!!._id)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF388E3C)
                            )
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("I'm Safe - Deactivate Signal")
                        }
                    }
                }
            }

            if (state.activeSignal == null) {
                // Message input
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Emergency Message") },
                    placeholder = { Text("Describe your situation...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5
                )

                // Current Location Display
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Your Current Location",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Lat: $currentLat",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Lon: $currentLon",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "This location will be shared with your emergency contacts",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                // Send SOS Button
                Button(
                    onClick = { showConfirmDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    ),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Icon(Icons.Default.Warning, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "SEND SOS SIGNAL",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Success/Error Messages
            if (state.success != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFC8E6C9)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = state.success!!,
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFF2E7D32)
                    )
                }
            }

            if (state.error != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = state.error!!,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Info
            Text(
                text = "⚠️ Use this feature only in real emergencies. False alarms may result in penalties.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }

    // Confirmation Dialog
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm SOS Signal") },
            text = { Text("Are you sure you want to send a distress signal? This will alert emergency contacts and share your location.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.sendDistressSignal(token, currentLat, currentLon, message)
                        showConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    )
                ) {
                    Text("Send SOS")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
