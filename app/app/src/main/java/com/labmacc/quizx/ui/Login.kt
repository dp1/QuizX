package com.labmacc.quizx.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labmacc.quizx.LoginViewModel
import com.labmacc.quizx.R
import com.labmacc.quizx.data.LoginRepository
import com.labmacc.quizx.ui.theme.hueca
import com.labmacc.quizx.ui.theme.wick
import kotlinx.coroutines.launch

@Composable
fun Login(
    vm: LoginViewModel,
    onComplete: () -> Unit = { }
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.skyblue))
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Image(
            painter = painterResource(id = R.drawable.q1x),
            contentDescription = "",
            modifier = Modifier.size(150.dp)
        )
        Text(text = "QUIZX", color = Color.White, fontFamily = hueca, fontSize = 70.sp)

        Spacer(modifier = Modifier.height(20.dp))
        Divider(color = Color.White, thickness = 2.dp)
        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            label = { Text(text = stringResource(R.string.prompt_email)) },
            value = vm.email.value,
            onValueChange = { vm.emailChanged(it) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            label = { Text(text = stringResource(R.string.prompt_password)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            value = vm.password.value,
            onValueChange = { vm.passwordChanged(it) }
        )

        if (vm.registerMode.value) {
            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                label = { Text(text = stringResource(R.string.prompt_name)) },
                value = vm.name.value,
                onValueChange = { vm.nameChanged(it) }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            if (!vm.registerMode.value) {
                Button(modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
                    shape = RoundedCornerShape(50.dp),
                    onClick = { vm.login() }
                ) {
                    Text(
                        text = stringResource(R.string.action_sign_in),
                        color = Color.White,
                        fontFamily = wick,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
                shape = RoundedCornerShape(50.dp),
                onClick = { vm.register() }
            ) {
                Text(
                    text = stringResource(R.string.action_register),
                    color = Color.White,
                    fontFamily = wick,
                    fontSize = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(25.dp))
        Divider(color = Color.White, thickness = 2.dp)

        if (vm.loginResult.value.attempted) {
            vm.loginResult.value.error?.let {
                Text(text = stringResource(it))
            }
            vm.loginResult.value.success?.let {
                onComplete()
            }
        }
    }
}

@Preview
@Composable
fun PreviewLogin() {
    Login(LoginViewModel(LoginRepository.instance))
}
