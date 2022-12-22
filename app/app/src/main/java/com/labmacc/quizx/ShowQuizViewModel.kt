package com.labmacc.quizx

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.labmacc.quizx.data.LoginRepository
import com.labmacc.quizx.data.QuizRepository
import com.labmacc.quizx.data.model.Quiz
import kotlinx.coroutines.launch
import com.labmacc.quizx.data.util.Result

class ShowQuizViewModel(private val quizRepository: QuizRepository) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                LoginViewModel(
                    LoginRepository.instance
                )
            }
        }

        const val TAG = "ShowQuizVM"
    }

    val quiz = mutableStateOf(Quiz())

    fun loadQuiz(uuid: String) {
        Log.i(TAG, "Requesting quiz $uuid")
        viewModelScope.launch {
            val res = quizRepository.getQuiz(uuid)
            if (res is Result.Success) {
                quiz.value = res.data
                Log.i(TAG, "Received quiz. VM ${System.identityHashCode(this)} Quiz $quiz")
            } else if (res is Result.Error){
                Log.w(TAG, "Failed to receive quiz. ${res.exception}")
            }
        }
    }
}