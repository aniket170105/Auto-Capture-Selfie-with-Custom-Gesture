package com.example.camera

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

fun saveToStorage(bitmap: Bitmap,lam : () -> Unit,
                  context: Context){
    //////////////////////////No need for the if statement without it it store image in DCIM folder
    val image_name = "ani_${System.currentTimeMillis()}.jpg"
    var fos : OutputStream? = null
    if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q){
        context.contentResolver?.also {resolver->
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, image_name)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
            val imageUri : Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues)
            fos = imageUri?.let{
                resolver.openOutputStream(it)
            }
        }
    }
    else{
        val imagesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val image = File(imagesDirectory, image_name)
        fos = FileOutputStream(image)
    }
    fos?.use{
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
//        Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()
    }
//    lam()
    afterTakingPhoto(lam,context)
}

fun afterTakingPhoto(lam : () -> Unit,
                     context: Context){
    lam()
    val builder = AlertDialog.Builder(context)
    builder.setTitle("Photo Captured")
    builder.setMessage("Your photo has been captured successfully.")
    builder.setPositiveButton("OK") { dialog, _ ->
        // Code to resume the app's functionality after the user clicks OK
        lam()
        dialog.dismiss()
    }
    val dialog = builder.create()
    dialog.show()
    val handler = Handler()
    handler.postDelayed({
        if (dialog.isShowing) {
            dialog.dismiss()
            lam()
        }
    }, 6000) // 6 seconds
}
