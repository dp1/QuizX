package com.labmacc.quizx.data

import android.net.Uri
import com.labmacc.quizx.data.util.Result

class QuizRepository(
    val firestoreDataSource: CloudFirestoreDataSource,
    val storageDataSource: FirebaseStorageDataSource
) {
    companion object {
        val instance by lazy {
            QuizRepository(
                CloudFirestoreDataSource(),
                FirebaseStorageDataSource()
            )
        }
    }

    suspend fun createQuiz(authorId: String, fileUri: Uri, answer: String): Result<Unit> {

        return when (val result = storageDataSource.uploadImage(fileUri)) {
            is Result.Success -> {
                val photoUri = result.data
                firestoreDataSource.createQuiz(authorId, photoUri, answer)
            }
            is Result.Error -> {
                Result.Error(result.exception)
            }
        }

//        storageDataSource.uploadImage(fileUri, { uri ->
//            Log.i("QuizR", "Uploaded image to $uri")
//
////            firestoreDataSource.createQuiz(authorId, uri, answer)
//
//        }, {
//            Log.w("QuizR", "Failed to upload image")
//        })
    }
}
