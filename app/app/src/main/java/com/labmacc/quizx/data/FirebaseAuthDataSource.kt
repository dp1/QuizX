package com.labmacc.quizx.data

import com.google.firebase.auth.FirebaseAuth
import com.labmacc.quizx.data.model.LoggedInUser
import com.labmacc.quizx.data.util.Result
import kotlinx.coroutines.tasks.await
import java.io.IOException

class FirebaseAuthDataSource {
    private val auth = FirebaseAuth.getInstance()

    suspend fun register(username: String, password: String): Result<LoggedInUser> {
        return try {
            val res = auth.createUserWithEmailAndPassword(username, password).await()
            res.user?.let {
                Result.Success(LoggedInUser(uuid = it.uid))
            } ?: Result.Error(IOException("Null user"))
        } catch (e: Throwable) {
            return Result.Error(IOException("Error creating user", e))
        }
    }

    suspend fun login(username: String, password: String): Result<LoggedInUser> {
        return try {
            val res = auth.signInWithEmailAndPassword(username, password).await()
            res.user?.let {
                Result.Success(LoggedInUser(uuid = it.uid))
            } ?: Result.Error(IOException("Null user"))
        } catch (e: Throwable) {
            Result.Error(IOException("Error logging in", e))
        }
    }

    fun restoreLogin(): LoggedInUser? {
        return auth.currentUser?.let {
            LoggedInUser(uuid = it.uid)
        }
    }

    fun signOut() {
        auth.signOut()
    }
}