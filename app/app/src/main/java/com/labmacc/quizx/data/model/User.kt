package com.labmacc.quizx.data.model

data class User(
    val uuid: String = "",
    val displayName: String = "",
    val score: Int = 0,
    val pendingChallenges: List<String> = listOf<String>()
)