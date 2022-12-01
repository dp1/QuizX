package com.labmacc.quizx.data

import com.labmacc.quizx.data.model.LoggedInUser
import com.labmacc.quizx.data.model.User

class LoginRepository(
    val authDataSource: FirebaseAuthDataSource,
    val firestoreDataSource: CloudFirestoreDataSource)
{
    var loggedInUser: LoggedInUser? = null
        private set

    var user: User? = null
        private set

    suspend fun register(email: String, password: String, name: String): Result<User> {
        val result = authDataSource.register(email, password)
        if (result is Result.Success) {
            loggedInUser = result.data
            val fsResult = firestoreDataSource.createUser(result.data.uuid, name)
            if (fsResult is Result.Success) {
                user = fsResult.data
            }
            return fsResult
        }
        return Result.Error(Exception("Login failed"))
    }

    suspend fun login(username: String, password: String): Result<User> {
        val result = authDataSource.login(username, password)
        if (result is Result.Success) {
            loggedInUser = result.data
            val fsResult = firestoreDataSource.getUser(result.data.uuid)
            if (fsResult is Result.Success) {
                user = fsResult.data
            }
            return fsResult
        }
        return Result.Error(Exception("asd"))
    }
}