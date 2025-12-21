package com.example.mynoteapp.routing

import HomeScreen
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
import com.example.mynoteapp.routing.screens.Drawer.PdfScreen

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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerHeader()  // ← User can pick image here

                NavigationDrawerItem(
                    label = { Text("Home") },
                    selected = false,
                    onClick = {
                        navController.navigate("home")
                        scope.launch { drawerState.close() }
                    }
                )

                NavigationDrawerItem(
                    label = { Text("PDF") },
                    selected = false,
                    onClick = {
                        navController.navigate("pdf")
                        scope.launch { drawerState.close() }
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Log Out") },
                    selected = false,
                    onClick = {
                        navController.navigate("notes")
                        scope.launch { drawerState.close() }
                    }
                )

            }
        }
    )
    {

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("My Note App") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },

            // ⭐ FIXED HERE — we pass onProfileClick
            bottomBar = {
                BottomNavBar(
                    navController = navController,
                    onProfileClick = {
                        scope.launch { drawerState.open() }  // OPEN DRAWER FROM PROFILE BUTTON
                    }
                )
            }
        ) { paddingValues ->

            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("home") { HomeScreen(navController) }
                composable("pdf") { PdfScreen(navController) }
                composable("notes") { NotesScreen(navController) }
            }
        }
    }
}
