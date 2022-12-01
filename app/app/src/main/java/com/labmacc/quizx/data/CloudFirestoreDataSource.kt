package com.labmacc.quizx.data

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.labmacc.quizx.data.model.User
import kotlinx.coroutines.tasks.await
import java.io.IOException

class CloudFirestoreDataSource {
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
}