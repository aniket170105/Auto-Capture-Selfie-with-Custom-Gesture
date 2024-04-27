package com.example.camera

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import com.example.camera.ml.Detect
import com.example.camera.ml.HeartDetect
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.system.measureTimeMillis

fun whichModelChoosed(which_model : String,
                      controller: LifecycleCameraController,
                      context : Context,
                      lam: () -> Unit) : Unit{
    if(which_model == "Select Option"){
        return
    }
    else if(which_model == "Smile              "){
        return
    }
    else if(which_model == "Victory           "){
        detectObjectAndSavePhotoVic(context,controller,lam)
    }
    else if(which_model == "Heart              "){
        detectObjectAndSavePhotoHeart(context,controller,lam)
    }
    else if(which_model == "Thumbs Up    "){
        detectObjectAndSavePhotoThumb(context, controller, lam)
    }
}

fun detectObjectAndSavePhotoVic(context : Context,controller: LifecycleCameraController,
                                     lam : () -> Unit)
//                                saveToStorage: (Bitmap, () -> Unit, Context) -> Unit // Inject saveToStorage function)
    {
    val imagePre = ImageProcessor.Builder().add(
        ResizeOp(320, 320,
            ResizeOp.ResizeMethod.NEAREST_NEIGHBOR)
    ).build()
    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
//                    super.onCaptureSuccess(image)
                val matrix = Matrix().apply {
                    postRotate(image.imageInfo.rotationDegrees.toFloat())
                }
                val rotatedBitmap = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.width,
                    image.height,
                    matrix,
                    true
                )
                val img = Bitmap.createScaledBitmap(rotatedBitmap, 320, 320, false)
                val byteBuffer = convertBitmapToByteBuffer(img)
                val tensorBuffer = convertByteBufferToTensorBuffer(byteBuffer, img.width, img.height)
                var flag : Boolean = false
                val outputs = model_victory.process(tensorBuffer)
                val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray
                outputFeature0.forEachIndexed { index, fl ->
                    if(fl>0.7){
                        flag = true
                    }
                }
                if(flag){
//                    lam()
                    saveToStorage(rotatedBitmap,lam,context)
                }
                image.close()
            }
        }
    )
}

fun detectObjectAndSavePhotoHeart(context : Context,controller: LifecycleCameraController,
                             lam : () -> Unit) {
    val imagePre = ImageProcessor.Builder().add(
        ResizeOp(320, 320,
            ResizeOp.ResizeMethod.NEAREST_NEIGHBOR)
    ).build()
    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
//                    super.onCaptureSuccess(image)
                val matrix = Matrix().apply {
                    postRotate(image.imageInfo.rotationDegrees.toFloat())
                }
                val rotatedBitmap = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.width,
                    image.height,
                    matrix,
                    true
                )
                val img = Bitmap.createScaledBitmap(rotatedBitmap, 320, 320, false)
                val byteBuffer = convertBitmapToByteBuffer(img)
                val tensorBuffer = convertByteBufferToTensorBuffer(byteBuffer, img.width, img.height)
                var flag : Boolean = false
                val outputs = model_heart.process(tensorBuffer)
                val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray
                outputFeature0.forEachIndexed { index, fl ->
                    if(fl>0.7){
                        flag = true
                    }
                }
                if(flag){
//                    lam()
                    saveToStorage(rotatedBitmap,lam,context)
                }
                image.close()
            }
        }
    )
}

fun detectObjectAndSavePhotoThumb(context : Context,controller: LifecycleCameraController,
                                lam : () -> Unit) {
    val imagePre = ImageProcessor.Builder().add(
        ResizeOp(320, 320,
            ResizeOp.ResizeMethod.NEAREST_NEIGHBOR)
    ).build()
    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
//                    super.onCaptureSuccess(image)
                val matrix = Matrix().apply {
                    postRotate(image.imageInfo.rotationDegrees.toFloat())
                }
                val rotatedBitmap = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.width,
                    image.height,
                    matrix,
                    true
                )
                val img = Bitmap.createScaledBitmap(rotatedBitmap, 320, 320, false)
                val byteBuffer = convertBitmapToByteBuffer(img)
                val tensorBuffer = convertByteBufferToTensorBuffer(byteBuffer, img.width, img.height)
                var flag : Boolean = false
                val outputs = model_thumb.process(tensorBuffer)
                val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray
                outputFeature0.forEachIndexed { index, fl ->
                    if(fl>0.7){
                        flag = true
                    }
                }
                if(flag){
//                    lam()
                    saveToStorage(rotatedBitmap,lam,context)
                }
                image.close()
            }
        }
    )
}


fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
    val imgData = ByteBuffer.allocateDirect(4 * bitmap.width * bitmap.height * 3)
    imgData.order(ByteOrder.nativeOrder())
    val intValues = IntArray(bitmap.width * bitmap.height)
    bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
    var pixel = 0
    for (i in 0 until bitmap.width) {
        for (j in 0 until bitmap.height) {
            val `val` = intValues[pixel++]
            imgData.putFloat(((`val` shr 16 and 0xFF) / 255.0f))
            imgData.putFloat(((`val` shr 8 and 0xFF) / 255.0f))
            imgData.putFloat(((`val` and 0xFF) / 255.0f))
        }
    }
    return imgData
}

fun convertByteBufferToTensorBuffer(byteBuffer: ByteBuffer, width: Int, height: Int): TensorBuffer {
    val shape = intArrayOf(1, width, height, 3)
    val tensorBuffer = TensorBuffer.createFixedSize(shape, DataType.FLOAT32)
    tensorBuffer.loadBuffer(byteBuffer)
    return tensorBuffer
}
