package com.labmacc.quizx.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.labmacc.quizx.R
import com.labmacc.quizx.RankingViewModel
import com.labmacc.quizx.data.LoginRepository
import com.labmacc.quizx.data.RankingRepository
import com.labmacc.quizx.data.model.User

@Composable
fun Ranking(vm: RankingViewModel) {
    val users = vm.ranking

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        for (i in 0 until users.size) {
            RankingUser(i, users[i])
        }
    }
}

@Composable
fun RankingUser(idx: Int, user: User) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(5.dp)) {
        Text(text = stringResource(R.string.ranking_pos, idx + 1))
        Text(text = user.displayName)
        Text(text = stringResource(R.string.ranking_score, user.score))
    }
}
