package com.labmacc.quizx

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.labmacc.quizx.data.CloudFirestoreDataSource
import com.labmacc.quizx.data.FirebaseAuthDataSource
import com.labmacc.quizx.data.LoginRepository
import kotlinx.coroutines.launch
import com.labmacc.quizx.data.util.Result
import com.labmacc.quizx.data.model.User

data class LoginResult(
    val success: User? = null,
    val error: Int? = null,
    val attempted: Boolean = false
)

data class LoginFormState(
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val nameError: Int? = null,
    val isDataValid: Boolean = false
)

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                LoginViewModel(
                    LoginRepository.instance
                )
            }
        }
    }

    private val loginFormState = mutableStateOf(LoginFormState())
    val loginResult = mutableStateOf(LoginResult())
    var registerMode = mutableStateOf(false)

    fun login(email: String, password: String) {
        viewModelScope.launch {

            val result = loginRepository.login(email, password)
            loginResult.value = LoginResult(attempted = true)
            if (result is Result.Success) {
                loginResult.value = LoginResult(success = result.data, attempted = true)
            } else {
                loginResult.value = LoginResult(error = R.string.login_failed, attempted = true)
            }
        }
    }

    fun register(email: String, password: String, name: String) {
        Log.i("prova","chiamata effettuata!")
        if (!registerMode.value) {
            enterRegisterMode()
            return
        }
        viewModelScope.launch {
            val result = loginRepository.register(email, password, name)
            loginResult.value = LoginResult(attempted = true)
            Log.i("prova",email+password+name)

            if (result is Result.Success) {
                loginResult.value = LoginResult(success = result.data, attempted = true)
                Log.i("prova","reg ok!")

            } else {
                loginResult.value = LoginResult(error = R.string.register_failed, attempted = true)
                Log.i("prova","reg ko!")

            }
        }
    }

    fun loginDataChanged(email: String, password: String, name: String) {
        if (!isEmailValid(email)) {
            loginFormState.value = LoginFormState(emailError = R.string.invalid_email)
        } else if (!isPasswordValid(password)) {
            loginFormState.value = LoginFormState(passwordError = R.string.invalid_password)
        } else if (registerMode.value && !isNameValid(name)) {
            loginFormState.value = LoginFormState(nameError = R.string.invalid_name)
        } else {
            loginFormState.value = LoginFormState(isDataValid = true)
        }
    }

    private fun enterRegisterMode() {
        registerMode.value = true
        //_registerMode.value = true
    }

    private fun isEmailValid(email: String): Boolean {
        return if (email.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
        } else {
            email.isNotBlank()
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    private fun isNameValid(name: String): Boolean {
        return name.length > 3
    }
}