package com.labmacc.quizx.data

import android.net.Uri
import com.labmacc.quizx.data.model.Quiz
import com.labmacc.quizx.data.model.User
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
    }

    suspend fun getQuiz(uuid: String): Result<Quiz> {
        return firestoreDataSource.getQuiz(uuid)
    }

    suspend fun getAuthor(authorid : String) : Result<User>{
        return firestoreDataSource.getUser(authorid)

    }

    fun listenForPendingChallenges(uuid: String, listener: (List<String>) -> Unit) {
        firestoreDataSource.listenForPendingChallenges(uuid, listener)
    }
}
