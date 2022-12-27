package com.labmacc.quizx

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil.imageLoader
import coil.request.ImageRequest
import com.labmacc.quizx.data.LoginRepository
import com.labmacc.quizx.data.QuizRepository
import com.labmacc.quizx.data.model.Quiz
import com.labmacc.quizx.data.model.User
import kotlinx.coroutines.launch
import com.labmacc.quizx.data.util.Result
import com.labmacc.quizx.data.model.SubmissionResult

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
    val image = mutableStateOf<Bitmap?>(null)
    val loading = mutableStateOf(false)
    val submissionResult = mutableStateOf<SubmissionResult?>(null)


    fun loadQuiz(uuid: String) {
        Log.i(TAG, "Requesting quiz $uuid")
        loading.value = true
        viewModelScope.launch {
            val res = quizRepository.getQuiz(uuid)
            if (res is Result.Success) {
                Log.i(TAG, "Received quiz.$quiz")
                val res2 = quizRepository.getAuthor(res.data.authorId)
                if (res2 is Result.Success) {
                    quiz.value = res.data
                    author.value = res2.data
                    Log.i(TAG, "Received author $author")

                    QuizXApplication.instance.also { context ->
                        val request = ImageRequest.Builder(context)
                            .data(res.data.imageUri)
                            .target(
                                onStart = {
                                    Log.i(TAG, "Image loading started")
                                },
                                onSuccess = {
                                    Log.i(TAG, "Image loading completed")
                                    image.value = it.toBitmapOrNull()
                                    loading.value = false
                                },
                                onError = {
                                    Log.w(TAG, "Image loading failed")
                                    loading.value = false
                                }
                            )
                            .build()

                        context.imageLoader.execute(request)
                    }

                } else if (res2 is Result.Error) {
                    Log.w(TAG, "Failed to receive author. ${res2.exception}")
                }
            } else if (res is Result.Error) {
                Log.w(TAG, "Failed to receive quiz. ${res.exception}")
            }
        }
    }

    fun resetAnswer() {
        submissionResult.value = null
    }

    fun sendAnswer(user_id: String, quiz_id: String, answer: String, coveredArea: Float) {
        quizRepository.sendAnswer(user_id, quiz_id, answer, coveredArea) {
            submissionResult.value = it
        }
    }
}
