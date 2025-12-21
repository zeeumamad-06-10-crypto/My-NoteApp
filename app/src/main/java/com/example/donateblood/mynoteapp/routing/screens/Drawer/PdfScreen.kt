package com.example.mynoteapp.routing.screens.Drawer

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun PdfScreen(navController: NavController) {
    val context = LocalContext.current

    var renderer by remember { mutableStateOf<PdfRenderer?>(null) }
    var fileDescriptor by remember { mutableStateOf<ParcelFileDescriptor?>(null) }
    var pageCount by remember { mutableStateOf(0) }

    var currentPageIndex by rememberSaveable { mutableStateOf(0) }
    var pageBitmap by remember { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }

    var scale by rememberSaveable { mutableStateOf(1f) }
    var offsetX by rememberSaveable { mutableStateOf(0f) }
    var offsetY by rememberSaveable { mutableStateOf(0f) }

    val configuration = LocalConfiguration.current
    val screenWidthPx = configuration.screenWidthDp * context.resources.displayMetrics.density

    // Load PDF from assets
    LaunchedEffect(Unit) {
        try {
            val assetFile = "Linkoln.pdf" // your file in assets
            val tempFile = File(context.cacheDir, assetFile)
            context.assets.open(assetFile).use { input ->
                tempFile.outputStream().use { output -> input.copyTo(output) }
            }

            val pfd = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
            fileDescriptor = pfd
            renderer = PdfRenderer(pfd)
            pageCount = renderer?.pageCount ?: 0
        } catch (e: Exception) {
            Log.e("PdfViewer", "Failed to load PDF", e)
        }
    }

    // Render page and auto-scale to full width
    LaunchedEffect(currentPageIndex, renderer) {
        renderer?.let {
            pageBitmap = renderPageToBitmap(it, currentPageIndex)
            pageBitmap?.let { bitmap ->
                // Set scale to fill screen width
                scale = (screenWidthPx / bitmap.width).coerceIn(1f, 8f)
                offsetX = 0f
                offsetY = 0f
            }
        }
    }

    var cumulativeDragX by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Pinch zoom & pan
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(1f, 8f)
                    offsetX += pan.x
                    offsetY += pan.y
                }
            }
            // Horizontal swipe to change page
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { cumulativeDragX = 0f },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        cumulativeDragX += dragAmount.x
                        if (scale > 1f) {
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        }
                    },
                    onDragEnd = {
                        if (cumulativeDragX > 150f && currentPageIndex > 0) currentPageIndex--
                        else if (cumulativeDragX < -150f && currentPageIndex < pageCount - 1) currentPageIndex++
                        cumulativeDragX = 0f
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
        } ?: Text(
            "Loading PDF...",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            try { renderer?.close() } catch (_: Exception) {}
            try { fileDescriptor?.close() } catch (_: Exception) {}
        }
    }
}

// Function to render PDF page to bitmap
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
