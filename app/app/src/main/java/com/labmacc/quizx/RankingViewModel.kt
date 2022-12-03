package com.labmacc.quizx

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.labmacc.quizx.data.CloudFirestoreDataSource
import com.labmacc.quizx.data.RankingRepository
import com.labmacc.quizx.data.model.User

class RankingViewModel(private val rankingRepository: RankingRepository) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                RankingViewModel(
                    RankingRepository(
                        firestoreDataSource = CloudFirestoreDataSource()
                    )
                )
            }
        }
    }

    private val _ranking = MutableLiveData<List<User>>()
    val ranking: LiveData<*> = _ranking

    init {
        rankingRepository.listenForRatingChanges { _ranking.value = it }
    }
}