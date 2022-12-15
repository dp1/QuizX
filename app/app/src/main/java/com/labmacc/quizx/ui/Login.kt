package com.labmacc.quizx.ui

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.labmacc.quizx.LoginResult
import com.labmacc.quizx.LoginViewModel
import com.labmacc.quizx.R
import com.labmacc.quizx.RankingActivity
import com.labmacc.quizx.data.util.Result
import com.labmacc.quizx.ui.theme.hueca
import com.labmacc.quizx.ui.theme.wick

@Composable
fun Login(vm: LoginViewModel) {

    vm.registerMode = remember{ mutableStateOf(false) }
    val username = remember { mutableStateOf(TextFieldValue()) }
    val password = remember { mutableStateOf(TextFieldValue()) }
    val display_name = remember { mutableStateOf(TextFieldValue()) }


    Column(verticalArrangement = Arrangement.Top,
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
            value = username.value,
            onValueChange = { username.value = it;
                vm.loginDataChanged(username.value.toString(), password.value.toString(), display_name.value.toString())
            }
        )
        Log.i("user", username.value.toString())
        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            label = { Text(text = "Password") },
            value = password.value,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            onValueChange = { password.value = it;
                vm.loginDataChanged(username.value.toString(), password.value.toString(), display_name.value.toString())
            })
        if (vm.registerMode.value) {
            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                label = { Text(text = "Display Name") },
                value = display_name.value,
                onValueChange = { display_name.value = it;
                    vm.loginDataChanged(username.value.toString(), password.value.toString(), display_name.value.toString())
                })
        }
        Spacer(modifier = Modifier.height(20.dp))
        if(!vm.registerMode.value) {
            Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                Button(
                    onClick = {vm.login(username.value.text,password.value.text) },
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
                onClick = {
                    vm.register(username.value.text,password.value.text,display_name.value.text)
                          },
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
        if(vm.loginResult.value.attempted){
            if(vm.loginResult.value.success == null){
                Text(text = "ERROR REGISTRATION")
            }
            else {
                val context = LocalContext.current
                context.startActivity(Intent(context, RankingActivity::class.java))

            }
        }

    }


}