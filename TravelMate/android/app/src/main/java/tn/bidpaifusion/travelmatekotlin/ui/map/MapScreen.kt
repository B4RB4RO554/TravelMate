@file:OptIn(ExperimentalMaterial3Api::class)

package tn.bidpaifusion.travelmatekotlin.ui.map

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.unit.dp
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat



@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MapScreen(navController: NavController) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val coroutineScope = rememberCoroutineScope()

    var userLat by remember { mutableStateOf<Double?>(null) }
    var userLon by remember { mutableStateOf<Double?>(null) }
    var htmlContent by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    val geoapifyKey = "656603e99e3a4c558d8eaf974553bd3d"

    // Get user location
    LaunchedEffect(Unit) {
        val permissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (permissionGranted) {
            try {
                val location = fusedLocationClient.lastLocation.await()
                userLat = location?.latitude ?: 36.8
                userLon = location?.longitude ?: 10.1
            } catch (e: SecurityException) {
                userLat = 36.8
                userLon = 10.1
            }
        } else {
            userLat = 36.8
            userLon = 10.1
        }
        htmlContent = generateMapHtml(userLat!!, userLon!!, geoapifyKey)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = destination,
            onValueChange = { destination = it },
            label = { Text("Enter Destination") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        )

        Button(
            onClick = {
                if (userLat != null && userLon != null && destination.isNotBlank()) {
                    coroutineScope.launch {
                        htmlContent = generateMapHtml(userLat!!, userLon!!, geoapifyKey, destination)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            Text("Get Route")
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (htmlContent.isNotEmpty()) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                factory = {
                    WebView(it).apply {
                        settings.javaScriptEnabled = true
                        webViewClient = WebViewClient()
                        loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
                    }
                }
            )
        }
    }
}

fun generateMapHtml(lat: Double, lon: Double, apiKey: String, destinationQuery: String? = null): String {
    val jsRouting = if (destinationQuery != null) """
        fetch("https://api.geoapify.com/v1/geocode/search?text=__DEST__&apiKey=__API_KEY__")
        .then(response => response.json())
        .then(geoData => {
            if (geoData.features.length > 0) {
                const dest = geoData.features[0].geometry.coordinates;
                const destLon = dest[0];
                const destLat = dest[1];

                L.marker([destLat, destLon]).addTo(map).bindPopup("Destination").openPopup();

                fetch("https://api.geoapify.com/v1/routing?waypoints=__LAT__,__LON__|"+destLat+","+destLon+"&mode=drive&apiKey=__API_KEY__")
                .then(r => r.json())
                .then(routeData => {
                    const coords = routeData.features[0].geometry.coordinates.map(c => [c[1], c[0]]);
                    L.polyline(coords, {color: 'blue'}).addTo(map);
                    map.fitBounds(L.polyline(coords).getBounds());
                });
            }
        });
    """ else ""

    return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css" />
            <script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"></script>
            <style>
                html, body { height: 100%; margin: 0; padding: 0; }
                #map { height: 100%; }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                const map = L.map('map').setView([__LAT__, __LON__], 14);
                L.tileLayer('https://maps.geoapify.com/v1/tile/osm-carto/{z}/{x}/{y}.png?apiKey=__API_KEY__', {
                    attribution: '&copy; OpenStreetMap contributors, Geoapify',
                    maxZoom: 20
                }).addTo(map);

                L.marker([__LAT__, __LON__]).addTo(map).bindPopup("You are here").openPopup();

                __ROUTING__
            </script>
        </body>
        </html>
    """.trimIndent()
        .replace("__LAT__", lat.toString())
        .replace("__LON__", lon.toString())
        .replace("__API_KEY__", apiKey)
        .replace("__DEST__", destinationQuery ?: "")
        .replace("__ROUTING__", jsRouting)
}
