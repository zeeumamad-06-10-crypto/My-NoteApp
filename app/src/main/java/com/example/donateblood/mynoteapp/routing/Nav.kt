package com.example.donateblood.mynoteapp.routing

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.donateblood.mynoteapp.routing.DataClass.NavDataClass
import com.example.donateblood.mynoteapp.routing.screens.Drawer.SignUpScreen
import com.example.donateblood.mynoteapp.routing.screens.Drawer.SigningInScreen
import com.example.donateblood.mynoteapp.routing.screens.HomeScreen
import com.example.donateblood.mynoteapp.routing.screens.ProfileScreen
import com.example.donateblood.mynoteapp.routing.screens.note.EditScreen
import com.example.donateblood.mynoteapp.routing.screens.note.NotesScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawerWithBottomBar(navController: NavController) {

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val drawerItems = listOf(
        NavDataClass("Home", Routes.Home.route, Icons.Default.Home),
        NavDataClass("Notes", Routes.Notes.route, Icons.Default.Notifications),
        NavDataClass("Sign In", Routes.SignIn.route, Icons.Default.Lock),
        NavDataClass("Sign Up", Routes.SignUp.route, Icons.Default.Lock),
        NavDataClass("Profile", Routes.Profile.route, Icons.Default.Person)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "Navigation",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.title) },
                        selected = false,
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(item.route) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Blood Donate") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            bottomBar = {
                BottomNavBar(navController)
            }
        ) { paddingValues ->

            NavHost(
                navController = navController,
                startDestination = Routes.Home.route,
                modifier = Modifier.padding(paddingValues)
            ) {

                composable(Routes.Home.route) {
                    HomeScreen(navController)
                }

                composable(Routes.Notes.route) {
                    NotesScreen(navController)
                }

                composable(Routes.Edit.route) {
                    EditScreen()
                }

                composable(Routes.SignIn.route) {
                    SigningInScreen(navController)
                }

                composable(Routes.SignUp.route) {
                    SignUpScreen(navController)
                }

                composable(Routes.Profile.route) {
                    ProfileScreen()
                }
            }
        }
    }
}
