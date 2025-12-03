package tn.bidpaifusion.travelmatekotlin.ui.trip

import android.app.Application
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
import androidx.navigation.NavController
import tn.bidpaifusion.travelmatekotlin.viewmodel.TripViewModel
import tn.bidpaifusion.travelmatekotlin.viewmodel.TripState
import tn.bidpaifusion.travelmatekotlin.ui.components.ConnectionStatusBanner
import tn.bidpaifusion.travelmatekotlin.ui.components.CacheIndicator
import tn.bidpaifusion.travelmatekotlin.ui.components.OfflineEmptyState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripListScreen(
    navController: NavController, 
    token: String, 
    viewModel: TripViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    val state by viewModel.state.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadTrips(token)
    }
    
    // Show snackbar for sync status
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(syncStatus) {
        syncStatus?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSyncStatus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Trips") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    // Sync button
                    IconButton(
                        onClick = { viewModel.syncPendingChanges(token) },
                        enabled = isOnline
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            "Sync",
                            tint = if (isOnline)
                                MaterialTheme.colorScheme.onSurface
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Navigate to create trip */ }
            ) {
                Icon(Icons.Default.Add, "Add Trip")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Connection status banner
            ConnectionStatusBanner(
                isOnline = isOnline,
                isSyncing = state is TripState.Loading,
                onSyncClick = { viewModel.syncPendingChanges(token) }
            )
            
            when (val currentState = state) {
                is TripState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(Modifier.height(8.dp))
                            Text("Loading trips...")
                        }
                    }
                }
                
                is TripState.Error -> {
                    if (currentState.cachedTrips?.isNotEmpty() == true) {
                        // Show cached data with error message
                        TripsList(
                            trips = currentState.cachedTrips,
                            fromCache = true,
                            errorMessage = currentState.message
                        )
                    } else {
                        OfflineEmptyState(
                            message = currentState.message,
                            onRetryClick = { viewModel.loadTrips(token) }
                        )
                    }
                }
                
                is TripState.Success -> {
                    if (currentState.trips.isEmpty()) {
                        EmptyTripsState()
                    } else {
                        TripsList(
                            trips = currentState.trips,
                            fromCache = currentState.fromCache
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TripsList(
    trips: List<tn.bidpaifusion.travelmatekotlin.data.models.Trip>,
    fromCache: Boolean,
    errorMessage: String? = null
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Show cache indicator if from cache
        if (fromCache) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Your Trips",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    CacheIndicator(fromCache = true)
                }
            }
        }
        
        // Error message if any
        errorMessage?.let {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = it,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
        
        items(trips) { trip ->
            TripCard(trip = trip)
        }
    }
}

@Composable
private fun TripCard(trip: tn.bidpaifusion.travelmatekotlin.data.models.Trip) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = trip.destination,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    Icons.Default.Flight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${trip.startDate} - ${trip.endDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (!trip.notes.isNullOrBlank()) {
                Text(
                    text = trip.notes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EmptyTripsState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.Flight,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                "No trips yet",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                "Start planning your next adventure!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
