package com.labmacc.quizx

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.labmacc.quizx.data.LoginRepository
import com.labmacc.quizx.data.QuizRepository
import com.labmacc.quizx.data.model.Quiz
import com.labmacc.quizx.data.model.User
import kotlinx.coroutines.launch
import com.labmacc.quizx.data.util.Result
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.Volley
import com.labmacc.quizx.data.model.SubmissionResult
import org.json.JSONArray
import org.json.JSONObject

class ShowQuizViewModel(private val quizRepository: QuizRepository, private val loginRepository: LoginRepository) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                ShowQuizViewModel(
                    QuizRepository.instance,
                    LoginRepository.instance
                )
            }
        }

        const val TAG = "ShowQuizVM"
    }

    val quiz = mutableStateOf(Quiz())
    val author = mutableStateOf(User())


    fun loadQuiz(uuid: String) {
        Log.i(TAG, "Requesting quiz $uuid")
        viewModelScope.launch {
            val res = quizRepository.getQuiz(uuid)
            if (res is Result.Success) {
                Log.i(TAG, "Received quiz.$quiz")
                val res2 = quizRepository.getAuthor(res.data.authorId)
                if (res2 is Result.Success) {
                    quiz.value = res.data
                    author.value = res2.data
                    Log.i(TAG, "Received author $author")
                } else if (res2 is Result.Error) {
                    Log.w(TAG, "Failed to receive author. ${res2.exception}")
                }
            } else if (res is Result.Error) {
                Log.w(TAG, "Failed to receive quiz. ${res.exception}")
            }
        }
    }

    val submissionResult = mutableStateOf<SubmissionResult?>(null)

    fun sendAnswer(user_id: String, quiz_id: String, answer: String) {
        quizRepository.sendAnswer(user_id, quiz_id, answer){
                submissionResult.value = it
        }
    }



}

