package com.example.mynoteapp.routing

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mynoteapp.routing.DataClass.NavDataClass
@Composable
fun BottomNavBar(
    navController: NavController,
    onProfileClick: () -> Unit
) {
    NavigationBar {

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("home") },
            icon = {
                Icon(Icons.Default.Home, contentDescription = "Home")
            },
            label = {
                Text("Home")
            }
        )


        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("notes") },
            icon = { Icon(Icons.Default.Notifications, contentDescription = "Notes") },
            label={
                Text("Note")
            }
        )

        // ⭐ Profile Button that opens Drawer
        NavigationBarItem(
            selected = false,
            onClick = { onProfileClick() },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label ={
               Text("Profile")
            }
        )
    }
}
