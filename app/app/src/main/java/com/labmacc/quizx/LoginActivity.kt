package com.labmacc.quizx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.labmacc.quizx.databinding.ActivityLoginBinding
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.labmacc.quizx.data.model.LoggedInUser
import com.labmacc.quizx.data.model.User

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val vm: LoginViewModel by viewModels { LoginViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val login = binding.login
        val register = binding.register
        val email = binding.email
        val password = binding.password
        val name = binding.name
        val loading = binding.loading

        vm.loginFormState.observe(this, Observer {
            if (it.isRegister) {
                name.visibility = View.VISIBLE
                login.visibility = View.INVISIBLE
            }
            else {
                name.visibility = View.INVISIBLE
                login.visibility = View.VISIBLE
            }

            login.isEnabled = it.isDataValid
            register.isEnabled = it.isDataValid

            if (it.emailError != null) {
                email.error = getString(it.emailError)
            }
            if (it.passwordError != null) {
                password.error = getString(it.passwordError)
            }
            if (it.nameError != null) {
                name.error = getString(it.nameError)
            }
        })

        vm.loginResult.observe(this, Observer {
            if (it.error != null) {
                showLoginFailed(it.error)
            }
            if (it.success != null) {
                updateUiWithUser(it.success)
            }
        })

        vm.progressBarvisible.observe(this, Observer {
            if (it) loading.visibility = View.VISIBLE
            else loading.visibility = View.GONE
        })

        email.addTextChangedListener { updateViewModel() }
        password.addTextChangedListener { updateViewModel() }
        name.addTextChangedListener { updateViewModel() }
        password.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE ->
                    vm.login(email.text.toString(), password.text.toString())
            }
            false
        }

        login.setOnClickListener {
            vm.login(email.text.toString(), password.text.toString())
        }
        register.setOnClickListener {
            vm.register(email.text.toString(), password.text.toString(), name.text.toString())
        }
    }

    private fun updateViewModel() {
        val email = binding.email
        val password = binding.password
        val name = binding.name
        vm.loginDataChanged(email.text.toString(), password.text.toString(), name.text.toString())
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    private fun updateUiWithUser(model: User) {
        val displayName = model.displayName
        Toast.makeText(
            applicationContext,
            "Welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }
}