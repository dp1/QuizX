package com.labmacc.quizx.data

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.labmacc.quizx.data.model.User
import com.labmacc.quizx.data.util.Result
import kotlinx.coroutines.tasks.await
import java.io.IOException

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
        val user = res.toObject<User>()!!
        return Result.Success(user)
    }

    fun listenForRatingChanges(listener: (List<User>) -> Unit) {
        db.collection("users").addSnapshotListener { value, error ->
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
}