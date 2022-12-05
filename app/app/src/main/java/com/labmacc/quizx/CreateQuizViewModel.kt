package com.labmacc.quizx

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.viewModelFactory

class CreateQuizViewModel : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            CreateQuizViewModel()
        }
    }
}