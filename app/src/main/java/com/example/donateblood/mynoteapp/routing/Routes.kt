package com.example.donateblood.mynoteapp.routing

sealed class Routes(val route: String) {
    object Home : Routes("home")
    object Notes : Routes("notes")
    object Edit : Routes("edit_page")
    object SignIn : Routes("sign_in")
    object SignUp : Routes("sign_up")
    object Profile : Routes("profile")
}
