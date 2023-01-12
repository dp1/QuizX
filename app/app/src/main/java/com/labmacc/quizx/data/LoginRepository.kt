package com.labmacc.quizx.data

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.labmacc.quizx.data.model.LoggedInUser
import com.labmacc.quizx.data.model.User
import com.labmacc.quizx.data.util.Result

class LoginRepository(
    val authDataSource: FirebaseAuthDataSource,
    val firestoreDataSource: CloudFirestoreDataSource)
{
    companion object {
        val instance by lazy {
            LoginRepository(
                authDataSource = FirebaseAuthDataSource(),
                firestoreDataSource = CloudFirestoreDataSource()
            )
        }
    }

    private var loggedInUser: LoggedInUser? = null

    var user = mutableStateOf<User?>(null)

    suspend fun register(email: String, password: String, name: String): Result<User> {
        val result = authDataSource.register(email, password)
        if (result is Result.Success) {
            loggedInUser = result.data
            val fsResult = firestoreDataSource.createUser(result.data.uuid, name)
            if (fsResult is Result.Success) {
                user.value = fsResult.data
            }
            return fsResult
        }
        return Result.Error(Exception("Registration failed"))
    }

    suspend fun login(username: String, password: String): Result<User> {
        val result = authDataSource.login(username, password)
        if (result is Result.Success) {
            loggedInUser = result.data
            val fsResult = firestoreDataSource.getUser(result.data.uuid)
            if (fsResult is Result.Success) {
                user.value = fsResult.data
            }
            return fsResult
        }
        return Result.Error(Exception("Login failed"))
    }

    suspend fun restoreLogin(): Result<User> {
        authDataSource.restoreLogin()?.let { saved ->
            loggedInUser = saved
            val fsResult = firestoreDataSource.getUser(saved.uuid)
            if (fsResult is Result.Success) {
                user.value = fsResult.data
                return fsResult
            }
        }
        return Result.Error(Exception("Failed to restore persisted login"))
    }

    fun signOut() {
        authDataSource.signOut()
        loggedInUser = null
        user.value = null
    }
}