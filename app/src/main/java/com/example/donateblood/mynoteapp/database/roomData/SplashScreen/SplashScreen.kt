package com.example.donateblood.mynoteapp.database.roomData.SplashScreen
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mynoteapp.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    // SplashScreen.kt
    LaunchedEffect(true) {
        delay(1500) // splash delay
        navController.navigate("main") {  // navigate to drawer + bottom bar
            popUpTo("splash") { inclusive = true }
        }
    }


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Image(
                painter = painterResource(id = R.drawable.img),
                contentDescription = "App Logo",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Welcome to My App",
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}
