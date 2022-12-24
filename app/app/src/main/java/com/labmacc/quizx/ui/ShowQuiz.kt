package com.labmacc.quizx.ui

import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.labmacc.quizx.CreateQuizViewModel
import com.labmacc.quizx.R
import com.labmacc.quizx.RankingViewModel
import com.labmacc.quizx.ShowQuizViewModel
import com.labmacc.quizx.data.LoginRepository
import com.labmacc.quizx.data.QuizRepository
import com.labmacc.quizx.data.RankingRepository
import com.labmacc.quizx.data.model.Quiz
import com.labmacc.quizx.data.model.User
import com.labmacc.quizx.ui.theme.hueca
import com.labmacc.quizx.ui.theme.wick

@Composable
fun ShowQuiz(quiz: Quiz, author : User) {
    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.skyblue))
        ) {
            Image(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .fillMaxSize()
                    .background(Color.LightGray),
                painter = rememberAsyncImagePainter(quiz.imageUri),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Row(
                modifier = Modifier
                    .imePadding()
                    .padding(10.dp)
                    .wrapContentHeight()
                    .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        textAlign = TextAlign.Center,
                        text = "CREATED BY ",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = wick
                        )
                    Spacer(Modifier.width(70.dp))
                    Text(
                        textAlign = TextAlign.Center,
                        text = author.displayName,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = wick
                        )
                }
            Row(
                modifier = Modifier
                    .imePadding()
                    .padding(10.dp)
                    .wrapContentHeight()
                    .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
            ) {

                TextField(
                    value = "",
                    onValueChange = { },
                    label = { Text("Answer") },
                    singleLine = true,
                    textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold),
                )
                Spacer(Modifier.size(10.dp))
                Button(
                    modifier = Modifier.wrapContentWidth(),
                    onClick = {  },
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = Color.Blue,
                    )
                ) {
                    Text("SEND", color = Color.White)
                }

            }
        }
    }
}


@Preview
@Composable
fun PreviewShowQuiz() {
    ShowQuiz(
        Quiz(uuid="90202690-6245-4ba2-b073-f1ac739627c8", authorId="gpUPW7TxG0bEvAq2hJDclVH7rwP2", imageUri="https://firebasestorage.googleapis.com/v0/b/animalx-1.appspot.com/o/images%2Fcf1c389f-1935-43d5-b299-3687dcbc475f.jpeg?alt=media&token=f18d833e-91b6-4ca6-9381-a4b0e3ffb1e1", correctAnswer="test", sentToUsers=true),
        User()
    )
}
