package com.labmacc.quizx.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.labmacc.quizx.data.model.Quiz

@Composable
fun ShowQuiz(quiz: Quiz) {
//    val quiz = remember { vm.quiz }
    Log.w("LOL", "ShowQuiz::recompose $quiz")

    Column() {
        Text(text = quiz.authorId)
        Text(text = quiz.uuid)
        Text(text = quiz.correctAnswer)
        Text(text = quiz.imageUri)
    }
}
