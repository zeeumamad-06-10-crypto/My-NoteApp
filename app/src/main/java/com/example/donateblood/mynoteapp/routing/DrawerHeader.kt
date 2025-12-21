package com.example.mynoteapp.routing

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.mynoteapp.R   // ✅ use your package name


@Composable
fun DrawerHeader() {
    // ✅ Correct delegate
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
    }

    Image(
        painter = if (imageUri != null) {
            rememberAsyncImagePainter(imageUri)
        } else {
            painterResource(id = R.drawable.myprofile)
        },
        contentDescription = "Profile Image",
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .clickable { launcher.launch("image/*") },
        contentScale = ContentScale.Crop
    )




        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { launcher.launch("image/*") }) {
            Text("Choose Image")
        }
    }

