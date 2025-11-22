package com.example.mynoteapp.routing.screens.note

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun NotesScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Notes Screen")
    }

    Button(
        onClick = {
            // Navigate to HomeScreen
            navController.navigate("home")
        }
    ) {
        Text("Go Home")
    }
}




