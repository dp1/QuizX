package com.labmacc.quizx.ui

import android.content.Context
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Lens
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.rememberImagePainter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun CameraView(
    onClick: (ImageCapture) -> Unit = { }
) {
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )

        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box( contentAlignment = Alignment.BottomCenter, modifier = Modifier.weight(5f)) {
            AndroidView( { previewView }, modifier = Modifier.fillMaxWidth().fillMaxHeight())
            IconButton(
                modifier = Modifier.padding(bottom = 5.dp),
                onClick = { onClick(imageCapture) },
                content = {
                    Icon(
                        imageVector = Icons.Sharp.Lens,
                        contentDescription = "Take picture",
                        tint = Color.White,
                        modifier = Modifier
                            .size(60.dp)
                            .padding(1.dp)
                            .border(1.dp, Color.White, CircleShape)
                    )
                }
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun ImageView(
    photoUri: Uri = Uri.EMPTY,
    onSubmit: () -> Unit = { }
) {
    Column {
        Image(
            painter = rememberImagePainter(photoUri),
            contentDescription = null,
        )

        Row( modifier = Modifier
            .padding(16.dp)
            .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically) {

            var text by remember { mutableStateOf("") }

            TextField(
                value = text ,
                onValueChange = { text = it },
                label = { Text("Enter your Answer") },
                maxLines = 2,
                textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold),
            )
            Spacer(Modifier.size(5.dp))
            Button( modifier = Modifier,
                onClick = onSubmit,
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = Color.Blue,
                )) {
                Text("SEND",color = Color.White)
            }

        }
    }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { cameraProvider ->
        cameraProvider.addListener({
            continuation.resume(cameraProvider.get())
        }, ContextCompat.getMainExecutor(this))
    }
}
