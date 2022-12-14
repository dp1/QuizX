package com.labmacc.quizx

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.rememberImagePainter
import com.labmacc.quizx.ui.CameraView
import com.labmacc.quizx.ui.ImageView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CreateQuizActivity : ComponentActivity() {
    companion object { const val TAG = "CreateQuizA" }

    private val vm: CreateQuizViewModel by viewModels { CreateQuizViewModel.Factory }

    private var shouldShowCamera = mutableStateOf(false)
    private var shouldShowPhoto = mutableStateOf(false)
    private lateinit var cameraExecutor: ExecutorService

    private lateinit var photoUri: Uri
    private lateinit var outputDirectory: File

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Log.i(TAG, "Permission granted")
            shouldShowCamera.value = true
        } else {
            Log.i(TAG, "Permission denied :(")
        }
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission already granted")
            shouldShowCamera.value = true
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            Log.i(TAG, "Showing permission rationale")
        } else {
            Log.i(TAG, "Launching permission request")
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun takePhoto(
        imageCapture: ImageCapture,
    ) {
        val photoFile = File(outputDirectory, "picture.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Take photo error:", exception)
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val uri = Uri.fromFile(photoFile)
                    Log.i(TAG, "Image captured: $uri")
                    shouldShowCamera.value = false
                    photoUri = uri
                    shouldShowPhoto.value = true
                }
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            if (shouldShowCamera.value) {
                CameraView(onClick = ::takePhoto)
            } else if (shouldShowPhoto.value) {
                ImageView(
                    photoUri = photoUri,
                    onSubmit = { }
                )
            }
        }

        requestPermissions()
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
