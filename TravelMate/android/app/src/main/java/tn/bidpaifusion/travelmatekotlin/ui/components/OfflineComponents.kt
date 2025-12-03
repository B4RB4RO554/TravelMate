package tn.bidpaifusion.travelmatekotlin.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import kotlinx.coroutines.delay

/**
 * Connection status banner that shows when offline or syncing
 */
@Composable
fun ConnectionStatusBanner(
    isOnline: Boolean,
    isSyncing: Boolean = false,
    pendingChanges: Int = 0,
    onSyncClick: () -> Unit = {}
) {
    AnimatedVisibility(
        visible = !isOnline || isSyncing || pendingChanges > 0,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        val backgroundColor = when {
            isSyncing -> Color(0xFF2196F3) // Blue
            !isOnline -> Color(0xFFFF9800) // Orange
            pendingChanges > 0 -> Color(0xFF4CAF50) // Green
            else -> Color.Transparent
        }
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = backgroundColor
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when {
                            isSyncing -> Icons.Default.Sync
                            !isOnline -> Icons.Default.CloudOff
                            else -> Icons.Default.CloudDone
                        },
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Text(
                        text = when {
                            isSyncing -> "Syncing data..."
                            !isOnline -> "You're offline"
                            pendingChanges > 0 -> "$pendingChanges changes to sync"
                            else -> ""
                        },
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                if (!isSyncing && isOnline && pendingChanges > 0) {
                    TextButton(
                        onClick = onSyncClick,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text("Sync Now")
                    }
                }
            }
        }
    }
}

/**
 * Badge showing if data is from cache
 */
@Composable
fun CacheIndicator(
    fromCache: Boolean,
    modifier: Modifier = Modifier
) {
    if (fromCache) {
        Surface(
            modifier = modifier,
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Storage,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    "Cached",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

/**
 * Sync status indicator for items
 */
@Composable
fun SyncStatusIcon(
    isSynced: Boolean,
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = if (isSynced) Icons.Default.CloudDone else Icons.Default.CloudOff,
        contentDescription = if (isSynced) "Synced" else "Not synced",
        tint = if (isSynced) Color(0xFF4CAF50) else Color(0xFFFF9800),
        modifier = modifier.size(16.dp)
    )
}

/**
 * Offline mode card shown when no data is available
 */
@Composable
fun OfflineEmptyState(
    message: String = "No data available offline",
    onRetryClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.CloudOff,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "You're Offline",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Button(onClick = onRetryClick) {
                Icon(Icons.Default.Refresh, null)
                Spacer(Modifier.width(8.dp))
                Text("Retry")
            }
        }
    }
}

/**
 * Last synced timestamp display
 */
@Composable
fun LastSyncedText(
    timestamp: Long?,
    modifier: Modifier = Modifier
) {
    if (timestamp != null) {
        val timeAgo = remember(timestamp) {
            val diff = System.currentTimeMillis() - timestamp
            when {
                diff < 60_000 -> "Just now"
                diff < 3600_000 -> "${diff / 60_000} min ago"
                diff < 86400_000 -> "${diff / 3600_000} hours ago"
                else -> "${diff / 86400_000} days ago"
            }
        }
        
        Text(
            text = "Last synced: $timeAgo",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier
        )
    }
}
