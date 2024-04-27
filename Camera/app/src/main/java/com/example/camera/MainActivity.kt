package com.example.camera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.example.camera.ui.theme.CameraTheme
import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role.Companion.DropdownList
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.camera.ml.Detect
import com.example.camera.ml.HeartDetect
import com.example.camera.ml.ThumbsDetect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
//import kotlinx.coroutines.flow.internal.NoOpContinuation.context
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
//import kotlin.coroutines.jvm.internal.CompletedContinuation.context

lateinit var model_victory : Detect
lateinit var model_heart : HeartDetect
lateinit var model_thumb : ThumbsDetect

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!hasRequiredPermissions()) {
            ActivityCompat.requestPermissions(
                this, CAMERAX_PERMISSIONS, 0
            )
        }
        model_victory = Detect.newInstance(this)
        model_heart = HeartDetect.newInstance(this)
        model_thumb = ThumbsDetect.newInstance(this)
        setContent {
            CameraTheme {
                val context = LocalContext.current
                val controller = remember {
                    LifecycleCameraController(applicationContext).apply {
                        setEnabledUseCases(
                            CameraController.IMAGE_CAPTURE or
                                    CameraController.VIDEO_CAPTURE
                        )
                    }
                }
                var isTakingPhoto by remember { mutableStateOf(false) }
                val coroutineScope = rememberCoroutineScope()
                var counter by remember { mutableIntStateOf(1) }
                var which_model by remember { mutableStateOf("Select") }

                var isVisible by remember { mutableStateOf(false) }
                val buttonText = if (isTakingPhoto) "Stop" else "Start"

                if (isTakingPhoto) {
                    counter++
                }
                if(isTakingPhoto && counter%120 == 0){
                    whichModelChoosed(which_model,controller,this,
                        {isTakingPhoto=!isTakingPhoto})
                }
                if (counter >= 130) {
                    counter = 1
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {
                    CameraPreview(
                        controller = controller,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.8f)
                            .offset(y = 64.dp) // Move the camera preview down by 64.dp
                            .clip(RoundedCornerShape(8.dp))
                    )
                    IconButton(
                        onClick = {
                            controller.cameraSelector =
                                if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                                    CameraSelector.DEFAULT_FRONT_CAMERA
                                } else CameraSelector.DEFAULT_BACK_CAMERA
                        },
                        modifier = Modifier
                            .offset(16.dp, 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cameraswitch,
                            contentDescription = "Switch camera",
                            tint = Color.White
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { isTakingPhoto = !isTakingPhoto },
                            modifier = Modifier
                                .offset(84.dp, 0.dp)
                                .background(color = Color.Black)
                        ) {
                            Text(
                                text = buttonText,
                                color = Color.Black
                            )
                        }
//                        IconButton(
//                            onClick = {
//                                isTakingPhoto = !isTakingPhoto
//                            },
//                            modifier = Modifier
//                                .offset(84.dp, 0.dp)
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.PhotoCamera,
//                                contentDescription = "Take photo",
//                                tint = Color.White
//                            )
//                        }
                    }
                    ClickableItemList({which_model = it
                        if(it=="Select Option"){
                            isTakingPhoto = false
                        }
                        else{
                            isTakingPhoto = true
                        }},Modifier.align(Alignment.TopEnd))
                    ImageListScreen(context = this@MainActivity, {isTakingPhoto = false})
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        model_victory.close()
        model_heart.close()
        model_thumb.close()
    }

    private fun hasRequiredPermissions(): Boolean {
        return CAMERAX_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private val CAMERAX_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
}

@Composable
fun ClickableItemList(lam : (String) -> Unit, modifier: Modifier) {
    var showItemList by remember { mutableStateOf(false) }
    val itemList = listOf("Select Option","Smile              ", "Victory           ", "Heart              ","Thumbs Up    ")

    var selectedItem by remember { mutableStateOf("Select Option") }

    Column(modifier = modifier
        .offset((-32).dp, 32.dp)
        .background(color = Color.Black)){
        Text(
            text = selectedItem,
            modifier = Modifier.clickable { showItemList = !showItemList },
            color = Color.White
        )
        AnimatedVisibility(visible = showItemList) {
            LazyColumn {
                items(itemList) { item ->
                    Text(
                        text = item,
                        modifier = Modifier
                            .clickable {
                                selectedItem = item
                                lam(item)
                                showItemList = !showItemList
                            }
//                            .padding(8.dp),
                        ,color = Color.White
                    )
                }
            }
        }
    }
}


