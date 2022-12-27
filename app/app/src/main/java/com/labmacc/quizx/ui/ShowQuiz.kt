package com.labmacc.quizx.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.labmacc.quizx.R
import com.labmacc.quizx.data.model.Quiz
import com.labmacc.quizx.data.model.User
import com.labmacc.quizx.ui.theme.wick
import com.labmacc.quizx.ShowQuizViewModel
import com.labmacc.quizx.ui.theme.hueca
import com.labmacc.quizx.ui.views.ScratchView
import kotlinx.coroutines.delay

@Composable
fun ShowQuiz(
    user: User,
    vm: ShowQuizViewModel,
    quiz: Quiz,
    author: User,
    onComplete: () -> Unit = {}
){
    var answer by remember{ mutableStateOf("")}
    if (vm.submissionResult.value != null) {
        Box(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(colorResource(R.color.skyblue))
        ) {
            LaunchedEffect(key1 = true) {
                delay(3000)
                onComplete()
            }
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Blue)
            ){
                if(vm.submissionResult.value!!.result) {
                    Column(modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            "Correct response!",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontFamily = hueca,
                            color = Color.White,
                            fontSize = 60.sp,
                        )
                        Text(
                            "Points earned : ${vm.submissionResult.value!!.score_obtained}",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontFamily = hueca,
                            color = Color.White,
                            fontSize = 60.sp
                        )
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            "Wrong response!",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontFamily = hueca,
                            color = Color.White,
                            fontSize = 60.sp,
                        )
                        Text(
                            "Points lost : ${vm.submissionResult.value!!.score_obtained}",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontFamily = hueca,
                            color = Color.White,
                            fontSize = 60.sp
                        )
                    }
                }
            }
        }
    } else {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(R.color.skyblue))
            ) {
                var scratchView by remember { mutableStateOf<ScratchView?>(null) }
                AndroidView(modifier = Modifier.weight(1f),
                    factory = { context -> ScratchView(context).apply { scratchView = this } },
                    update = { view ->
                        vm.image.value?.let {
                            view.setImage(it)
                        }
                    }
                )
                Row(
                    modifier = Modifier
                        .imePadding()
                        .padding(10.dp)
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                ){
                    TextField(
                        value = answer,
                        onValueChange = { answer = it },
                        label = { Text("Answer") },
                        singleLine = true,
                        textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold),
                    )
                    Spacer(Modifier.size(10.dp))
                    Button(
                        modifier = Modifier.wrapContentWidth(),
                        onClick = {
                            val coveredArea = scratchView?.coveredArea() ?: 0f
                            vm.sendAnswer(user.uuid, quiz.uuid, answer, coveredArea)
                        },
                        colors = ButtonDefaults.textButtonColors(
                            backgroundColor = Color.Blue,
                        )
                    ) {
                        Text("SEND", color = Color.White)
                    }

                }
            }

            if (vm.loading.value) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

/*
@Preview
@Composable
fun PreviewShowQuiz() {
    ShowQuiz(
        Quiz(uuid="90202690-6245-4ba2-b073-f1ac739627c8", authorId="gpUPW7TxG0bEvAq2hJDclVH7rwP2", imageUri="https://firebasestorage.googleapis.com/v0/b/animalx-1.appspot.com/o/images%2Fcf1c389f-1935-43d5-b299-3687dcbc475f.jpeg?alt=media&token=f18d833e-91b6-4ca6-9381-a4b0e3ffb1e1", correctAnswer="test", sentToUsers=true),
        User()
    )
}
*/