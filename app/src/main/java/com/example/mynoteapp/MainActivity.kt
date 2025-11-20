package com.example.mynoteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent


import com.example.mynoteapp.routing.AppDrawerWithBottomBar




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {


            AppDrawerWithBottomBar()






        }
    }
}
