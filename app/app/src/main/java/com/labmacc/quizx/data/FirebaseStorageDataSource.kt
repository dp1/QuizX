package com.labmacc.quizx.data

import android.net.Uri
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import com.labmacc.quizx.data.util.Result
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseStorageDataSource {
    companion object { const val TAG = "FirebaseStorageDS" }

    private val storage = Firebase.storage
    private var imagesRef = storage.reference.child("images")

    suspend fun uploadImage(fileUri: Uri): Result<Uri> {
        val metadata = storageMetadata { contentType = "image/jpeg" }
        val fileName = "${UUID.randomUUID().toString()}.jpeg"
        val fileRef = imagesRef.child(fileName)
        val uploadTask = fileRef.putFile(fileUri, metadata)

        val x = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    Log.w(TAG, "Imgae upload failed: $it")
                }
            }
            fileRef.downloadUrl
        }.await()

        return Result.Success(x)
    }
}
