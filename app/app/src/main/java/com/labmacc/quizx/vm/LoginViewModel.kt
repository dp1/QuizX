package com.labmacc.quizx.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.labmacc.quizx.R
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
    val registerMode = mutableStateOf(false)

    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val name = mutableStateOf("")

    fun prepare() {
        loginFormState.value = LoginFormState()
        loginResult.value = LoginResult()
        registerMode.value = false
        email.value = ""
        password.value = ""
        name.value = ""
    }

    fun emailChanged(value: String) {
        email.value = value
        loginDataChanged()
    }

    fun passwordChanged(value: String) {
        password.value = value
        loginDataChanged()
    }

    fun nameChanged(value: String) {
        name.value = value
        loginDataChanged()
    }

    fun login() {
        viewModelScope.launch {
            val result = loginRepository.login(email.value, password.value)
            loginResult.value = LoginResult(attempted = true)
            if (result is Result.Success) {
                loginResult.value = LoginResult(success = result.data, attempted = true)
            } else {
                loginResult.value = LoginResult(error = R.string.login_failed, attempted = true)
            }
        }
    }

    fun register() {
        if (!registerMode.value) {
            registerMode.value = true
            return
        }
        viewModelScope.launch {
            val result = loginRepository.register(email.value, password.value, name.value)
            loginResult.value = LoginResult(attempted = true)
            Log.i("prova", "$email $password $name")
            if (result is Result.Success) {
                loginResult.value = LoginResult(success = result.data, attempted = true)
                Log.i("prova", "reg ok!")
            } else {
                loginResult.value = LoginResult(error = R.string.register_failed, attempted = true)
                Log.i("prova", "reg ko!")

            }
        }
    }

    private fun loginDataChanged() {
        if (!isEmailValid(email.value)) {
            loginFormState.value = LoginFormState(emailError = R.string.invalid_email)
        } else if (!isPasswordValid(password.value)) {
            loginFormState.value = LoginFormState(passwordError = R.string.invalid_password)
        } else if (registerMode.value && !isNameValid(name.value)) {
            loginFormState.value = LoginFormState(nameError = R.string.invalid_name)
        } else {
            loginFormState.value = LoginFormState(isDataValid = true)
        }
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