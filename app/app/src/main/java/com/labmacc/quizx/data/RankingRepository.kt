package com.labmacc.quizx.data

import com.labmacc.quizx.data.model.User

class RankingRepository(val firestoreDataSource: CloudFirestoreDataSource) {
    fun listenForRatingChanges(listener: (List<User>) -> Unit) {
        firestoreDataSource.listenForRatingChanges(listener)
    }
}