package tn.bidpaifusion.travelmatekotlin.ui.splash

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import tn.bidpaifusion.travelmatekotlin.R

@Composable
fun SplashScreen(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(220.dp)
                        .padding(16.dp)
                )
            }
        }

        // Navigate after 2.5s
        LaunchedEffect(true) {
            Handler(Looper.getMainLooper()).postDelayed({
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }, 2500)
        }
    }
}
