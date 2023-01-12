package com.labmacc.quizx.vm

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.labmacc.quizx.data.LoginRepository
import com.labmacc.quizx.data.QuizRepository
import com.labmacc.quizx.data.model.User
import kotlinx.coroutines.launch
import com.labmacc.quizx.data.util.Result

enum class UploadState {
    None,
    Pending,
    Complete,
    Failed
}

class CreateQuizViewModel(
    private val loginRepository: LoginRepository,
    private val quizRepository: QuizRepository,
    ) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                CreateQuizViewModel(
                    LoginRepository.instance,
                    QuizRepository.instance
                )
            }
        }
    }

    val uploadState = mutableStateOf(UploadState.None)

    fun createQuiz(authorId: String, fileUri: Uri, answer: String) {
        viewModelScope.launch {
            uploadState.value = UploadState.Pending

            val res = quizRepository.createQuiz(authorId, fileUri, answer)

            if (res is Result.Success) {
                uploadState.value = UploadState.Complete
            } else {
                uploadState.value = UploadState.Failed
            }
        }
    }

    fun currentUser(): MutableState<User?> {
        return loginRepository.user
    }
}