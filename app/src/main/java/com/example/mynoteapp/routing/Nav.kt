package com.example.mynoteapp.routing

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mynoteapp.routing.DataClass.NavDataClass
import com.example.mynoteapp.routing.screens.*
import com.example.mynoteapp.routing.screens.Drawer.SignUpScreen
import com.example.mynoteapp.routing.screens.Drawer.SigningInScreen
import com.example.mynoteapp.routing.screens.note.EditScreen
import com.example.mynoteapp.routing.screens.note.NotesScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawerWithBottomBar() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val drawerItems = listOf(
        NavDataClass("Home", "home", Icons.Default.Home),
        NavDataClass("Notes", "notes", Icons.Default.Notifications),
        NavDataClass("SignIN", "sign_in", Icons.Default.Star),
        NavDataClass("SignUp", "sign_up", Icons.Default.Star),
        NavDataClass("Profile", "profile", Icons.Default.Person),

    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Menu", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.title) },
                        selected = false,
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        onClick = {
                            navController.navigate(item.route)
                            scope.launch { drawerState.close() }
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("My Note App") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            bottomBar = { BottomNavBar(navController) } // Use the reusable bottom nav
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("home") { HomeScreen(navController) }
                composable("notes") { NotesScreen(navController) }
                composable("edit_page") { EditScreen() }
                composable("sign_in") { SigningInScreen() }
                composable("sign_up") { SignUpScreen() }
                composable("profile") { ProfileScreen() }
            }
        }
    }
}
