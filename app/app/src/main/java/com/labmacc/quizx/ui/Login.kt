package com.labmacc.quizx.ui

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labmacc.quizx.LoginViewModel
import com.labmacc.quizx.R
import com.labmacc.quizx.RankingActivity
import com.labmacc.quizx.data.LoginRepository
import com.labmacc.quizx.ui.theme.hueca
import com.labmacc.quizx.ui.theme.wick

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
        Image(
            painter = painterResource(id = R.drawable.q1x),
            contentDescription = "",
            modifier = Modifier.size(150.dp)
        )
        Text(text = "QUIZX", color = Color.White, fontFamily = hueca, fontSize = 70.sp)

        Spacer(modifier = Modifier.height(40.dp))
        Divider(color = Color.White, thickness = 2.dp)
        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            label = { Text(text = "Username") },
            value = vm.email.value,
            onValueChange = { vm.emailChanged(it) }
        )
        Log.i("user", vm.email.value)
        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            label = { Text(text = "Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            value = vm.password.value,
            onValueChange = { vm.passwordChanged(it) }
        )

        if (vm.registerMode.value) {
            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                label = { Text(text = "Display Name") },
                value = vm.name.value,
                onValueChange = { vm.nameChanged(it) }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (!vm.registerMode.value) {
            Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                Button(
                    onClick = { vm.login() },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "Login",
                        color = Color.White,
                        fontFamily = wick,
                        fontSize = 15.sp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            Button(
                onClick = { vm.register() },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "REGISTER",
                    color = Color.White,
                    fontFamily = wick,
                    fontSize = 15.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(25.dp))
        Divider(color = Color.White, thickness = 2.dp)

        if (vm.loginResult.value.attempted) {
            if (vm.loginResult.value.success == null) {
                Text(text = "ERROR REGISTRATION")
            } else {
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
