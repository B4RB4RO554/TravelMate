package tn.bidpaifusion.travelmatekotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import tn.bidpaifusion.travelmatekotlin.ui.login.LoginScreen
import tn.bidpaifusion.travelmatekotlin.ui.splash.SplashScreen
import tn.bidpaifusion.travelmatekotlin.ui.theme.TravelMateKotlinTheme
import tn.bidpaifusion.travelmatekotlin.ui.home.HomeScreen
import tn.bidpaifusion.travelmatekotlin.ui.map.MapScreen
import tn.bidpaifusion.travelmatekotlin.ui.navigation.AppNavGraph
import tn.bidpaifusion.travelmatekotlin.ui.trip.TripListScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TravelMateKotlinTheme {
                AppNavGraph()
            }
        }
    }
}
