package com.labmacc.quizx

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.labmacc.quizx.ui.Login

class LoginActivity : ComponentActivity() {
    private val vm: LoginViewModel by viewModels { LoginViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Login(vm, onComplete = {
                startActivity(Intent(this, RankingActivity::class.java))
            })
        }
    }
}