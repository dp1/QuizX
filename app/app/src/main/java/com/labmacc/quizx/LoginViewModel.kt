package com.labmacc.quizx

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.labmacc.quizx.data.FirebaseAuthDataSource
import com.labmacc.quizx.data.LoginRepository
import com.labmacc.quizx.data.model.LoggedInUser
import kotlinx.coroutines.launch
import com.labmacc.quizx.data.Result

data class LoginResult(
    val success: LoggedInUser? = null,
    val error: Int? = null
)

data class LoginFormState(
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val nameError: Int? = null,
    val isRegister: Boolean = false,
    val isDataValid: Boolean = false
)

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                LoginViewModel(
                    loginRepository = LoginRepository(
                        dataSource = FirebaseAuthDataSource()
                    )
                )
            }
        }
    }

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _progressVisible = MutableLiveData<Boolean>(false)
    val progressBarvisible: LiveData<Boolean> = _progressVisible

    private var isRegisterMode = false

    fun login(email: String, password: String) {
        _progressVisible.value = true
        viewModelScope.launch {
            val result = loginRepository.login(email, password)
            if (result is Result.Success) {
                _loginResult.value = LoginResult(success = result.data)
            } else {
                _loginResult.value = LoginResult(error = R.string.login_failed)
            }
            _progressVisible.value = false
        }
    }

    fun register(email: String, password: String) {
        if (!isRegisterMode) {
            enterRegisterMode()
            return
        }

        _progressVisible.value = true
        viewModelScope.launch {
            val result = loginRepository.register(email, password)
            if (result is Result.Success) {
                _loginResult.value = LoginResult(success = result.data)
            } else {
                _loginResult.value = LoginResult(error = R.string.register_failed)
            }
            _progressVisible.value = false
        }
    }

    fun loginDataChanged(email: String, password: String, name: String) {
        if (!isEmailValid(email)) {
            _loginForm.value = LoginFormState(emailError = R.string.invalid_email, isRegister = isRegisterMode)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password, isRegister = isRegisterMode)
        } else if (isRegisterMode && !isNameValid(name)) {
            _loginForm.value = LoginFormState(nameError = R.string.invalid_name, isRegister = isRegisterMode)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true, isRegister = isRegisterMode)
        }
    }

    private fun enterRegisterMode() {
        isRegisterMode = true
        val state = _loginForm.value ?: LoginFormState()
        _loginForm.value = state.copy(isRegister = true)
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