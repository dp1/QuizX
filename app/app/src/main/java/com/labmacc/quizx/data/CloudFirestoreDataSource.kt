package com.labmacc.quizx.data

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.labmacc.quizx.data.model.Quiz
import com.labmacc.quizx.data.model.User
import com.labmacc.quizx.data.util.Result
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.util.UUID

class CloudFirestoreDataSource {
    companion object { const val TAG = "FirestoreDS" }

    private val db = Firebase.firestore

    suspend fun createUser(uuid: String, displayName: String): Result<User> {
        return try {
            val user = User(uuid, displayName, 0, listOf<String>())
            db.collection("users").document(uuid).set(user).await()
            Result.Success(user)
        } catch (e: Throwable) {
            Result.Error(IOException("Error creating user", e))
        }
    }

    suspend fun getUser(uuid: String): Result<User> {
        val res = db.collection("users").document(uuid).get().await()
        val user = res.toObject<User>()

        return user?.let {
            Result.Success(user)
        } ?: Result.Error(IOException("Retrieved user is null"))
    }

    suspend fun createQuiz(authorId: String, photoUri: Uri, answer: String): Result<Unit> {
        return try {
            val uuid = UUID.randomUUID().toString()
            val quiz = Quiz(uuid, authorId, photoUri.toString(), answer)
            db.collection("quizzes").document(uuid).set(quiz).await()
            Result.Success(Unit)
        } catch (e : Throwable) {
            Result.Error(IOException("Error creating quiz", e))
        }
    }

    suspend fun getQuiz(uuid: String): Result<Quiz> {
        val res = db.collection("quizzes").document(uuid).get().await()
        val quiz = res.toObject<Quiz>()

        return quiz?.let {
            Result.Success(quiz)
        } ?: Result.Error(IOException("Retrieved quiz is null"))
    }

    fun listenForRatingChanges(listener: (List<User>) -> Unit) {
        db.collection("users").orderBy("score", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if (error != null) {
                Log.w(TAG, "Listen failed", error)
            } else {
                val users = ArrayList<User>()
                for (doc in value!!) {
                    users.add(doc.toObject<User>())
                }
                listener(users)
            }
        }
    }

    fun listenForPendingChallenges(uuid: String, listener: (List<String>) -> Unit) {
        db.collection("users").whereEqualTo("uuid", uuid).addSnapshotListener { value, error ->
            if (error != null) {
                Log.w(TAG, "Listen failed", error)
            } else {
                val user = value!!.documents[0].toObject<User>()
                user?.let {
                    listener(it.pendingChallenges)
                }
            }
        }
    }
}