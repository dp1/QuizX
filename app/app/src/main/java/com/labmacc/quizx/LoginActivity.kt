package com.labmacc.quizx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.labmacc.quizx.databinding.ActivityLoginBinding
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.labmacc.quizx.data.model.LoggedInUser
import com.labmacc.quizx.data.model.User
import com.labmacc.quizx.ui.Login

class LoginActivity : ComponentActivity() {
    private val vm: LoginViewModel by viewModels { LoginViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { Login(vm = vm) }
    }
}