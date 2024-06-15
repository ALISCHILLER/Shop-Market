package com.msa.eshop.ui.screen.profileRestPassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.msa.componentcompose.ui.component.lottiefile.LoadingAnimate
import com.msa.eshop.ui.component.weightC.RoundedIconTextField

@Composable
fun ProfileRestPasswordScreen(
    modifier: Modifier = Modifier,
    viewModel: RestPasswordViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var password by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var repeatNewPassword by remember { mutableStateOf("") }
    Box {
        Column(
            modifier = Modifier
                .width(328.dp)
                .height(490.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            RoundedIconTextField(
                value = password,
                onValueChange = { password = it },
                label = "رمزعبور فعلی",
                icon = Icons.Default.Lock,
                isPassword = true,
                modifier = Modifier.padding(6.dp)
            )
            RoundedIconTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = "رمزعبورجدید",
                icon = Icons.Default.Lock,
                isPassword = true,
                modifier = Modifier.padding(6.dp)
            )
            RoundedIconTextField(
                value = repeatNewPassword,
                onValueChange = { repeatNewPassword = it },
                label = "تکراررمزعبور",
                icon = Icons.Default.Lock,
                isPassword = true,
                modifier = Modifier.padding(6.dp)
            )
            Button(
                onClick = {
                    viewModel.restPasswordRequest(
                        password = password,
                        newPassword = newPassword,
                        repeatNewPassword = repeatNewPassword
                    )
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            )
            {
                Text("ثبت ")
            }
        }
        if (state.isLoading) {
            LoadingAnimate()
            // Blurred background
        }
    }


}