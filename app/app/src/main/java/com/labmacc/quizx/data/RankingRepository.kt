package com.labmacc.quizx.data

import com.labmacc.quizx.data.model.User

class RankingRepository(val firestoreDataSource: CloudFirestoreDataSource) {
    companion object {
        val instance by lazy {
            RankingRepository(
                firestoreDataSource = CloudFirestoreDataSource()
            )
        }
    }

    fun listenForRatingChanges(listener: (List<User>) -> Unit) {
        firestoreDataSource.listenForRatingChanges(listener)
    }
}