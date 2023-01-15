package com.labmacc.quizx.ui

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labmacc.quizx.R
import com.labmacc.quizx.ui.theme.wick
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onComplete: () -> Unit = { }
) = Box(
    Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .background(colorResource(R.color.skyblue))
) {

    val scale = remember {
        Animatable(0.0f)
    }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.7f,
            animationSpec = tween(1500, easing = {
                OvershootInterpolator(4f).getInterpolation(it)
            })
        )
        delay(2000)
        onComplete()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
            modifier = Modifier.fillMaxWidth().padding(top = 300.dp),
            text = "Shake to create a quiz",
            textAlign = TextAlign.Center,
            color = Color.LightGray,
            fontFamily = wick,
            fontSize = 20.sp
        )
            Image(
                painter = painterResource(id = R.drawable.q1),
                contentDescription = "",
                alignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(bottom = 190.dp)
                    .scale(scale.value)
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}
