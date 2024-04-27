package com.example.camera

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.camera.core.CameraSelector
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun ImageListScreen(context : Context,
                    lam : () -> Unit
) {
    var toShow by remember { mutableStateOf(false) }
    if (toShow) {
        BackHandler(onBack = { toShow = false })
    }
    IconButton(
        onClick = {
            toShow = !toShow
            lam()
        },
        modifier = Modifier
            .offset(250.dp, 778.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Image,
            contentDescription = "Switch camera",
            tint = Color.White
        )
    }
    if(toShow) {
        val images = getSavedImages(context)
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Saved Images")
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (images.isEmpty()) {
//                        Text(text = "Oh ho No Photo Captured")
                    } else {
                        items(images) {
                            SavedImageItem(image = it)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SavedImageItem(image: ImageBitmap) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Image(
            bitmap = image,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentScale = ContentScale.Crop
        )
    }
}

fun getSavedImages(context: Context): List<ImageBitmap> {
    val images = mutableListOf<ImageBitmap>()
    val directory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        Environment.DIRECTORY_PICTURES
    } else {
        Environment.DIRECTORY_DCIM
    }
    val directoryPath = Environment.getExternalStoragePublicDirectory(directory)
    val files = directoryPath.listFiles()
    files?.forEach { file ->
        if (file.isFile && file.extension == "jpg") {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            bitmap?.let {
                images.add(it.asImageBitmap())
            }
        }
    }
    return images
}