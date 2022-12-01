package com.labmacc.quizx.data

import com.labmacc.quizx.data.model.LoggedInUser

class LoginRepository(val dataSource: FirebaseAuthDataSource) {
    var user: LoggedInUser? = null
        private set

    suspend fun register(username: String, password: String): Result<LoggedInUser> {
        val result = dataSource.register(username, password)
        if (result is Result.Success) {
            user = result.data
        }
        return result
    }

    suspend fun login(username: String, password: String): Result<LoggedInUser> {
        val result = dataSource.login(username, password)
        if (result is Result.Success) {
            user = result.data
        }
        return result
    }
}