package com.labmacc.quizx

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.labmacc.quizx.ui.*
import com.labmacc.quizx.ui.theme.hueca
import kotlinx.coroutines.delay
import java.io.File
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
                CameraView(onCapture = ::takePhoto)
            } else if (shouldShowPhoto.value) {
                if (vm.uploadState.value == UploadState.Complete) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .background(colorResource(R.color.skyblue))
                    ) {

                        LaunchedEffect(key1 = true) {
                            delay(1000)
                            finish()
                        }
                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color = Color.Blue)
                        ) {

                            Text(
                                "Quiz Created!",
                                Modifier.padding(100.dp),
                                textAlign = TextAlign.Center,
                                fontFamily = hueca,
                                color = Color.White,
                                fontSize = 60.sp
                            )
                        }
                    }
                }
                else {
                    val answer = remember { mutableStateOf("") }

                    ImageView(
                        photoUri = photoUri,
                        onSubmit = {
                            val authorId = vm.currentUser().value?.uuid ?: "Author"
                            vm.createQuiz(authorId, photoUri, answer.value)
                        },
                        answer = answer.value,
                        onAnswerChanged = { answer.value = it },
                        uploadState = vm.uploadState.value
                    )
                }
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
