package tn.bidpaifusion.travelmatekotlin.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import tn.bidpaifusion.travelmatekotlin.data.api.Place
import tn.bidpaifusion.travelmatekotlin.viewmodel.EmergencyViewModel
import kotlin.math.pow
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyServicesScreen(
    onNavigateBack: () -> Unit,
    token: String,
    viewModel: EmergencyViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    
    // Location is assumed to be available from previous screen
    // In production, use FusedLocationProviderClient
    var currentLat by remember { mutableStateOf(36.8065) } // Tunis default
    var currentLon by remember { mutableStateOf(10.1815) }

    LaunchedEffect(Unit) {
        viewModel.loadEmergencyNumbers("Tunisia")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emergency Services") },
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
        ) {
            // Emergency Numbers Card
            if (state.emergencyNumbers.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "🚨 Emergency Numbers",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        state.emergencyNumbers.forEach { (service, number) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("$service:", style = MaterialTheme.typography.bodyLarge)
                                TextButton(onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:$number")
                                    }
                                    context.startActivity(intent)
                                }) {
                                    Icon(Icons.Default.Phone, null, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text(number, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Service Type Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = state.selectedType == "hospital",
                    onClick = {
                        viewModel.selectPlaceType("hospital")
                        viewModel.searchPlaces(token, "hospital", currentLat, currentLon)
                    },
                    label = { Text("🏥 Hospitals") }
                )
                FilterChip(
                    selected = state.selectedType == "police",
                    onClick = {
                        viewModel.selectPlaceType("police")
                        viewModel.searchPlaces(token, "police", currentLat, currentLon)
                    },
                    label = { Text("👮 Police") }
                )
                FilterChip(
                    selected = state.selectedType == "fuel",
                    onClick = {
                        viewModel.selectPlaceType("fuel")
                        viewModel.searchPlaces(token, "fuel", currentLat, currentLon)
                    },
                    label = { Text("⛽ Fuel") }
                )
            }

            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.places) { place ->
                        PlaceCard(place = place, userLat = currentLat, userLon = currentLon)
                    }

                    if (state.places.isEmpty() && !state.isLoading) {
                        item {
                            Text(
                                text = "No places found nearby. Try another category.",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceCard(place: Place, userLat: Double, userLon: Double) {
    val context = LocalContext.current
    val distance = calculateDistance(userLat, userLon, place.lat, place.lon)

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            // Open in maps
            val uri = Uri.parse("geo:${place.lat},${place.lon}?q=${place.name}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = place.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                if (place.phone != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.Phone,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = place.phone!!,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "%.1f km".format(distance),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    Icons.Default.Directions,
                    contentDescription = "Get directions",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadius = 6371.0 // km
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = Math.sin(dLat / 2).pow(2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2).pow(2)
    val c = 2 * Math.atan2(sqrt(a), sqrt(1 - a))
    return earthRadius * c
}
