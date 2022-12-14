package com.labmacc.quizx.data.model

data class Quiz(
    val uuid: String = "",
    val authorId: String = "",
    val imageUri: String = "",
    val correctAnswer: String = "",
    val sentToUsers: Boolean = false,
    val correctSubmissions: Int = 0,
    val wrongSubmissions: Int = 0
)
