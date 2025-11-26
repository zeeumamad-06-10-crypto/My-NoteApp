package com.example.donateblood.mynoteapp.routing.screens.Drawer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.donateblood.mynoteapp.routing.Routes

@Composable
fun SigningInScreen(navController: NavController) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Sign In",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Forgot Password
            Text(
                text = "Forgot Password?",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { /* TODO: Navigate to Forgot Password */ }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Sign In Button
            Button(
                onClick = { /* TODO: Sign In action */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Sign In")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sign Up link
            Row {
                Text(text = "Don't have an account? ")
                Text(
                    text = "Sign Up",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        navController.navigate(Routes.SignUp.route)
                    }
                )
            }
        }
    }
}
