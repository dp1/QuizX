package com.labmacc.quizx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.labmacc.quizx.databinding.ActivityCreateQuizBinding

class CreateQuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateQuizBinding
    private val vm: CreateQuizViewModel by viewModels { CreateQuizViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}