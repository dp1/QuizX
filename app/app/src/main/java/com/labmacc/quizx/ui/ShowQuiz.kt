package com.labmacc.quizx.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import com.labmacc.quizx.data.model.Quiz

@Composable
fun ShowQuiz(quiz: Quiz) {
    Column() {
        Text(text = quiz.authorId)
        Text(text = quiz.uuid)
        Text(text = quiz.correctAnswer)
        Text(text = quiz.imageUri)
    }
}
