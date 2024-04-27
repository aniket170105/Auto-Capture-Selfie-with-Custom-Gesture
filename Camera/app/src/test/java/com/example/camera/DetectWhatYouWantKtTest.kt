package com.example.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.internal.verification.VerificationModeFactory.times
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import com.example.camera.ml.HeartDetect
import com.example.camera.ml.Detect
import com.example.camera.ml.ThumbsDetect
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever

//@RunWith(RobolectricTestRunner::class)
class DetectWhatYouWantKtTest{
    @Test
    fun `test whichModelChoosed with Select Option`() {
        val mockContext = mock(Context::class.java)
        val mockController = mock(LifecycleCameraController::class.java)
        val mockLambda = mock<() -> Unit>()
        whichModelChoosed("Select Option", mockController, mockContext, mockLambda)
        // Assertion: No further actions should be taken
        verify(mockLambda, never()).invoke()
    }

    @Test
    fun `test whichModelChoosed with Smile`() {
        val mockContext = mock(Context::class.java)
        val mockController = mock(LifecycleCameraController::class.java)
        val mockLambda = mock<() -> Unit>()

        whichModelChoosed("Smile              ", mockController, mockContext, mockLambda)

        // Assertion: No further actions should be taken
        verify(mockLambda, never()).invoke()
    }

    @Test
    fun `test whichModelChoosed with Victory`() {
        val mockContext = mock(Context::class.java)
        val mockController = mock(LifecycleCameraController::class.java)
        val mockLambda = mock<() -> Unit>()

        whichModelChoosed("Victory           ", mockController, mockContext, mockLambda)

        // Assertion: detectObjectAndSavePhotoVic should be called
        verify(mockController).takePicture(any(), any())
    }

    @Test
    fun `test whichModelChoosed with Heart`() {
        val mockContext = mock(Context::class.java)
        val mockController = mock(LifecycleCameraController::class.java)
        val mockLambda = mock<() -> Unit>()

        whichModelChoosed("Heart              ", mockController, mockContext, mockLambda)

        // Assertion: detectObjectAndSavePhotoHeart should be called
        verify(mockController).takePicture(any(), any())
    }

    @Test
    fun `test whichModelChoosed with Thumbs Up`() {
        val mockContext = mock(Context::class.java)
        val mockController = mock(LifecycleCameraController::class.java)
        val mockLambda = mock<() -> Unit>()
        val next_function = mock<(Context,LifecycleCameraController, () -> Unit) -> Unit>()

        whichModelChoosed("Thumbs Up    ", mockController, mockContext, mockLambda)

        // Assertion: detectObjectAndSavePhotoThumb should be called
        verify(mockController).takePicture(any(), any())
    }

    @Test
    fun `convertByteBufferToTensorBuffer should convert byte buffer to tensor buffer`() {
        // Arrange
        val byteBuffer = ByteBuffer.allocateDirect(320 * 320 * 3 * 4)
        byteBuffer.order(ByteOrder.nativeOrder())
        val width = 320
        val height = 320

        // Act
        val tensorBuffer = convertByteBufferToTensorBuffer(byteBuffer, width, height)

        // Assert
        assertEquals(1, tensorBuffer.shape[0])
        assertEquals(width, tensorBuffer.shape[1])
        assertEquals(height, tensorBuffer.shape[2])
        assertEquals(3, tensorBuffer.shape[3])
        assertEquals(DataType.FLOAT32, tensorBuffer.dataType)
    }

    @Test
    fun `test detectObjectAndSavePhotoHeart is taking photo`() {
        // Mock dependencies
        val mockContext = mock(Context::class.java)
        val mockController = mock(LifecycleCameraController::class.java)
        val mockLambda = mock<() -> Unit>()

        detectObjectAndSavePhotoHeart(mockContext, mockController, mockLambda)
        // Assertion: detectObjectAndSavePhotoThumb should be called
        verify(mockController).takePicture(any(), any())
    }

    @Test
    fun `test detectObjectAndSavePhotoVic is taking photo`() {
        // Mock dependencies
        val mockContext = mock(Context::class.java)
        val mockController = mock(LifecycleCameraController::class.java)
        val mockLambda = mock<() -> Unit>()

        detectObjectAndSavePhotoHeart(mockContext, mockController, mockLambda)
        // Assertion: detectObjectAndSavePhotoThumb should be called
        verify(mockController).takePicture(any(), any())
    }

    @Test
    fun `test detectObjectAndSavePhotoThumb is taking photo`() {
        // Mock dependencies
        val mockContext = mock(Context::class.java)
        val mockController = mock(LifecycleCameraController::class.java)
        val mockLambda = mock<() -> Unit>()

        detectObjectAndSavePhotoHeart(mockContext, mockController, mockLambda)
        // Assertion: detectObjectAndSavePhotoThumb should be called
        verify(mockController).takePicture(any(), any())
    }

}