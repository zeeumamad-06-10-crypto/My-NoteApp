package com.example.mynoteapp.routing.screens.Drawer

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfScreen(navController: NavController) {
    val context = LocalContext.current

    var pdfUri by remember { mutableStateOf<Uri?>(null) }
    var renderer by remember { mutableStateOf<PdfRenderer?>(null) }
    var fileDescriptor by remember { mutableStateOf<ParcelFileDescriptor?>(null) }
    var pageCount by remember { mutableStateOf(0) }

    var currentPageIndex by rememberSaveable { mutableStateOf(0) }
    var pageBitmap by remember { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }

    var scale by rememberSaveable { mutableStateOf(1f) }
    var offsetX by rememberSaveable { mutableStateOf(0f) }
    var offsetY by rememberSaveable { mutableStateOf(0f) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        pdfUri = it
    }

    // Load PDF
    LaunchedEffect(pdfUri) {
        val uri = pdfUri ?: return@LaunchedEffect
        try { context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: Exception) {}

        renderer?.close()
        fileDescriptor?.close()
        pageBitmap = null
        currentPageIndex = 0

        val pfd = context.contentResolver.openFileDescriptor(uri, "r") ?: return@LaunchedEffect
        fileDescriptor = pfd

        try {
            val pdfRenderer = PdfRenderer(pfd)
            renderer = pdfRenderer
            pageCount = pdfRenderer.pageCount
        } catch (e: Exception) {
            Log.e("PdfViewer", "Renderer error", e)
        }
    }

    // Render page
    LaunchedEffect(currentPageIndex, renderer) {
        renderer?.let { pageBitmap = renderPageToBitmap(it, currentPageIndex) }
    }

    var cumulativeDragX by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Pinch zoom
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(1f, 8f)
                    offsetX += pan.x
                    offsetY += pan.y
                }
            }
            // Horizontal swipe → change page
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { cumulativeDragX = 0f },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        cumulativeDragX += dragAmount.x
                        if (scale > 1f) {
                            // If zoomed, also pan
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        }
                    },
                    onDragEnd = {
                        // Change page if swipe threshold crossed
                        if (cumulativeDragX > 150f && currentPageIndex > 0) currentPageIndex--
                        else if (cumulativeDragX < -150f && currentPageIndex < pageCount - 1) currentPageIndex++
                        cumulativeDragX = 0f
                    }
                )
            }
            // Double-tap → page change
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { offset ->
                        if (offset.x < size.width / 2 && currentPageIndex > 0) currentPageIndex--
                        else if (offset.x >= size.width / 2 && currentPageIndex < pageCount - 1) currentPageIndex++
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        pageBitmap?.let {
            Image(
                bitmap = it,
                contentDescription = "PDF Page",
                modifier = Modifier.graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                )
            )
        } ?: Text("Open a PDF", textAlign = TextAlign.Center)

        Button(
            onClick = { launcher.launch(arrayOf("application/pdf")) },
            modifier = Modifier.align(Alignment.TopCenter).padding(16.dp)
        ) { Text("Open PDF") }

//        Row(
//            modifier = Modifier.align(Alignment.BottomCenter).padding(20.dp),
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            Button(onClick = { if (currentPageIndex > 0) currentPageIndex-- }) { Text("Prev") }
//            Button(onClick = { if (currentPageIndex < pageCount - 1) currentPageIndex++ }) { Text("Next") }
//            Button(onClick = {
//                scale = 1f
//                offsetX = 0f
//                offsetY = 0f
//            }) { Text("Reset") }
//        }
    }

    DisposableEffect(Unit) {
        onDispose {
            try { renderer?.close() } catch (_: Exception) {}
            try { fileDescriptor?.close() } catch (_: Exception) {}
        }
    }
}

// Must be outside PdfScreen
suspend fun renderPageToBitmap(renderer: PdfRenderer, pageIndex: Int): androidx.compose.ui.graphics.ImageBitmap? =
    withContext(Dispatchers.IO) {
        var page: PdfRenderer.Page? = null
        try {
            page = renderer.openPage(pageIndex)
            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            bitmap.asImageBitmap()
        } catch (e: Exception) {
            Log.e("PdfViewer", "Failed to render page", e)
            null
        } finally {
            page?.close()
        }
    }

