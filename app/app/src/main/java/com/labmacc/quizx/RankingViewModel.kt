package com.labmacc.quizx

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.labmacc.quizx.data.LoginRepository
import com.labmacc.quizx.data.RankingRepository
import com.labmacc.quizx.data.model.User

class RankingViewModel(
    private val rankingRepository: RankingRepository,
    private val loginRepository: LoginRepository,
    ) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                RankingViewModel(
                    RankingRepository.instance,
                    LoginRepository.instance
                )
            }
        }
    }

    val ranking = mutableStateListOf<User>()

    init {
        rankingRepository.listenForRatingChanges {
            ranking.clear()
            ranking.addAll(it)
        }
    }

    fun currentUser(): User? {
        return loginRepository.user
    }
}