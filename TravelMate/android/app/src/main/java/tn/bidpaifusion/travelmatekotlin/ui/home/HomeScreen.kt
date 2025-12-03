package tn.bidpaifusion.travelmatekotlin.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class DashboardItem(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val gradientColors: List<Color>,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, userId: String?, token: String?) {
    val safeToken = token ?: ""
    
    val dashboardItems = listOf(
        DashboardItem(
            title = "My Trips",
            icon = Icons.Default.Flight,
            route = "trips/$safeToken",
            gradientColors = listOf(Color(0xFF667eea), Color(0xFF764ba2)),
            description = "Plan & manage trips"
        ),
        DashboardItem(
            title = "Map",
            icon = Icons.Default.Map,
            route = "map",
            gradientColors = listOf(Color(0xFF11998e), Color(0xFF38ef7d)),
            description = "Navigate & explore"
        ),
        DashboardItem(
            title = "Places",
            icon = Icons.Default.Restaurant,
            route = "poi/$safeToken",
            gradientColors = listOf(Color(0xFFf093fb), Color(0xFFf5576c)),
            description = "Restaurants & Hotels"
        ),
        DashboardItem(
            title = "Currency",
            icon = Icons.Default.CurrencyExchange,
            route = "currency/$safeToken",
            gradientColors = listOf(Color(0xFF4facfe), Color(0xFF00f2fe)),
            description = "Convert currencies"
        ),
        DashboardItem(
            title = "Emergency",
            icon = Icons.Default.LocalHospital,
            route = "emergency/$safeToken",
            gradientColors = listOf(Color(0xFFeb3349), Color(0xFFf45c43)),
            description = "Emergency services"
        ),
        DashboardItem(
            title = "Cultural Guide",
            icon = Icons.Default.Translate,
            route = "cultural",
            gradientColors = listOf(Color(0xFFee9ca7), Color(0xFFffdde1)),
            description = "Phrases & customs"
        ),
        DashboardItem(
            title = "SOS Signal",
            icon = Icons.Default.Warning,
            route = "distress/$safeToken",
            gradientColors = listOf(Color(0xFFd31027), Color(0xFFea384d)),
            description = "Send distress alert"
        ),
        DashboardItem(
            title = "Settings",
            icon = Icons.Default.Settings,
            route = "settings",
            gradientColors = listOf(Color(0xFF757F9A), Color(0xFFD7DDE8)),
            description = "App preferences"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("TravelMate", fontWeight = FontWeight.Bold)
                        Text(
                            "Your travel companion",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Profile */ }) {
                        Icon(Icons.Default.AccountCircle, "Profile")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Welcome Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.TravelExplore,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "Welcome back!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Ready for your next adventure?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Dashboard Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(dashboardItems) { item ->
                    DashboardCard(
                        item = item,
                        onClick = { navController.navigate(item.route) }
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardCard(item: DashboardItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(item.gradientColors)
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    item.icon,
                    contentDescription = item.title,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text(
                        item.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        item.description,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
