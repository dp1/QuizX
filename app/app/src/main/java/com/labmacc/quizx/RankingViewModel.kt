package com.labmacc.quizx

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.labmacc.quizx.data.CloudFirestoreDataSource
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

    private val _ranking = MutableLiveData<List<User>>()
    val ranking: LiveData<List<User>> = _ranking

    init {
        rankingRepository.listenForRatingChanges { _ranking.value = it }
    }

    fun currentUser(): User? {
        return loginRepository.user
    }
}