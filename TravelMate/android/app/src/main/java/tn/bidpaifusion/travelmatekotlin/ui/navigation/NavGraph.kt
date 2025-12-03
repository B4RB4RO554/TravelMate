package tn.bidpaifusion.travelmatekotlin.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import tn.bidpaifusion.travelmatekotlin.ui.login.LoginScreen
import tn.bidpaifusion.travelmatekotlin.ui.register.RegisterScreen
import tn.bidpaifusion.travelmatekotlin.ui.home.HomeScreen
import tn.bidpaifusion.travelmatekotlin.ui.map.MapScreen
import tn.bidpaifusion.travelmatekotlin.ui.trip.TripListScreen
import tn.bidpaifusion.travelmatekotlin.ui.splash.SplashScreen
import tn.bidpaifusion.travelmatekotlin.ui.screens.*

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(navController)
        }

        composable("map") {
            MapScreen(navController)
        }

        composable("register") {
            RegisterScreen(navController)
        }

        composable("login") {
            LoginScreen(navController)
        }

        composable(
            "home/{userId}/{token}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
                navArgument("token") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            val token = backStackEntry.arguments?.getString("token")
            HomeScreen(navController, userId, token)
        }

        composable(
            "trips/{token}",
            arguments = listOf(navArgument("token") { type = NavType.StringType })
        ) { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token") ?: ""
            TripListScreen(navController, token)
        }

        composable(
            "emergency/{token}",
            arguments = listOf(navArgument("token") { type = NavType.StringType })
        ) { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token") ?: ""
            EmergencyServicesScreen(
                onNavigateBack = { navController.popBackStack() },
                token = token
            )
        }

        composable(
            "currency/{token}",
            arguments = listOf(navArgument("token") { type = NavType.StringType })
        ) { backStackEntry ->
            CurrencyConverterScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            "distress/{token}",
            arguments = listOf(navArgument("token") { type = NavType.StringType })
        ) { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token") ?: ""
            DistressSignalScreen(
                onNavigateBack = { navController.popBackStack() },
                token = token
            )
        }

        composable(
            "poi/{token}",
            arguments = listOf(navArgument("token") { type = NavType.StringType })
        ) { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token") ?: ""
            POISuggestionsScreen(
                onNavigateBack = { navController.popBackStack() },
                token = token
            )
        }

        composable("cultural") {
            CulturalGuideScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("settings") {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNotifications = { navController.navigate("notification_settings") }
            )
        }

        composable("notification_settings") {
            NotificationSettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
