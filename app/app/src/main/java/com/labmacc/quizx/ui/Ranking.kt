package com.labmacc.quizx.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labmacc.quizx.R
import com.labmacc.quizx.RankingViewModel
import com.labmacc.quizx.data.LoginRepository
import com.labmacc.quizx.data.RankingRepository
import com.labmacc.quizx.data.model.User
import com.labmacc.quizx.ui.theme.hueca
import com.labmacc.quizx.ui.theme.wick

@Composable
fun Ranking(vm: RankingViewModel) {
    val users = vm.ranking
    val (currentUser, _) = remember { vm.currentUser() }
    var count = remember { mutableStateOf( 0) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        LazyColumn(
            modifier = Modifier
                .background(colorResource(R.color.skyblue))
                .fillMaxHeight()
                .weight(1f)
        ) {
            itemsIndexed(users, key = { _, user -> user.uuid }) { i, user ->
                RankingUser(i, user, currentUser)
            }
        }
        if(count.value > 0){
            Box(modifier = Modifier.background(colorResource(R.color.dark_sky)).fillMaxWidth()) {
                Button(
                    onClick = { /**/ },
                    colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(R.color.teal_700)),
                    modifier = Modifier.align(alignment = Alignment.Center).padding(5.dp)
                ) {
                    Text(
                        text = if (count.value == 1) "${count.value} CHALLENGE AVAILABLE" else "${count.value} CHALLENGES AVAILABLE" ,
                        color = Color.White
                    )
                }
            }
        }
    }

}

@Composable
fun RankingUser(idx: Int, user: User, currentUser: User?) {
    val background = if (currentUser?.uuid == user.uuid) colorResource(R.color.teal_700) else colorResource(R.color.blue)

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 0.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(background)
        .padding(16.dp)
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.ranking_pos, idx + 1),
            color = Color.LightGray,
            fontFamily = wick,
            fontSize = 20.sp
        )
        Text(
            modifier = Modifier.weight(6f),
            text = user.displayName,
            color = Color.LightGray,
            fontFamily = wick,
            fontSize = 20.sp
        )
        Text(
            modifier = Modifier.weight(3f),
            text = stringResource(R.string.ranking_score, user.score),
            color = Color.LightGray,
            fontFamily = wick,
            fontSize = 20.sp
        )
    }
}

@Preview
@Composable
fun PreviewRanking() {
    Ranking(RankingViewModel(RankingRepository.instance, LoginRepository.instance))
}
