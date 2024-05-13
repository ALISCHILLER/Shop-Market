package com.msa.eshop.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.msa.eshop.R
import com.msa.eshop.ui.component.weightC.RoundedIconTextField

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
   val viewModel: LoginViewModel = hiltViewModel()

    val currentSample = rememberSaveable { mutableStateOf<Boolean?>(null) }

    // بازیابی نام کاربری و رمز عبور از SharedPreferences
    val savedUsername = viewModel.getSavedUsername()
    val savedPassword = viewModel.getSavedPassword()

    val state by viewModel.state.collectAsState()
    val onReset = { currentSample.value = state.isLoading }
    if (state.isLoading) {

    }
    state.error?.let {

    }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            state.error?.let {

            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var username by remember { mutableStateOf(savedUsername) }
                var password by remember { mutableStateOf(savedPassword) }

                Card(
                    modifier = Modifier
                        .width(328.dp)
                        .padding(16.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logozar),
                            contentDescription = "logo",
                            modifier = Modifier
                                .size(110.dp, 82.dp)
                                .layoutId("logo")
                        )

                        RoundedIconTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = "کد ملی",
                            icon = Icons.Default.Person
                        )

                        RoundedIconTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "رمز عبور",
                            icon = Icons.Default.Lock,
                            isPassword = true
                        )

                        Text(
                            text = "رمز عبور خود را فراموش کرده اید؟",
                            modifier = Modifier.padding(8.dp)
                        )

                        Button(
                            onClick = {
                                viewModel.getToken(username, password)
                                // اینجا می‌توانید عملیات ورود را انجام دهید
                                // مثلا می‌توانید اطلاعات را به سرور ارسال کرده و ورود کاربر را بررسی کنید
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("بریم که خرید رو شروع کنیم")
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = "iconbutton"
                            )

                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {

    LoginScreen()
}