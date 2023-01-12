package com.labmacc.quizx

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.labmacc.quizx.data.LoginRepository
import com.labmacc.quizx.data.QuizRepository
import com.labmacc.quizx.data.RankingRepository
import com.labmacc.quizx.data.model.User
import kotlinx.coroutines.launch

class RankingViewModel(
    private val rankingRepository: RankingRepository,
    val loginRepository: LoginRepository,
    private val quizRepository: QuizRepository
    ) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                RankingViewModel(
                    RankingRepository.instance,
                    LoginRepository.instance,
                    QuizRepository.instance
                )
            }
        }
    }

    val loginViewModel = LoginViewModel(loginRepository)
    val showQuizViewModel = ShowQuizViewModel(quizRepository,loginRepository)

    val ranking = mutableStateListOf<User>()
    val numPendingChallenges = mutableStateOf(0)
    val nextPendingChallenge = mutableStateOf("")

    init {
        viewModelScope.launch { loginRepository.restoreLogin() }

        rankingRepository.listenForRatingChanges {
            ranking.clear()
            ranking.addAll(it)
        }
    }

    fun onLoggedIn() {
        loginRepository.user.value?.let { user ->
            quizRepository.listenForPendingChallenges(user.uuid) {
                numPendingChallenges.value = it.size
                nextPendingChallenge.value = if(it.isNotEmpty()) it[0] else ""
            }
        }
    }

    fun currentUser(): MutableState<User?> {
        return loginRepository.user
    }
}