package com.msa.eshop.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.msa.eshop.ui.component.button.ButtonBorderAnmation
import com.msa.eshop.ui.component.dialog.CustomDialog
import com.msa.eshop.ui.component.dialog.ErrorDialog
import com.msa.eshop.ui.component.dialog.ErrorWarning
import com.msa.eshop.ui.component.weightC.RoundedIconTextField
import com.msa.eshop.ui.theme.DIMENS_14dp
import com.msa.eshop.ui.theme.DIMENS_8dp
import com.msa.eshop.ui.theme.PlatinumSilver
import com.msa.eshop.ui.theme.RoyalPurple
import com.msa.eshop.ui.theme.RoyalRed
import com.msa.eshop.ui.theme.Typography

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val savedUsername = viewModel.getSavedUsername()
    val savedPassword = viewModel.getSavedPassword()
    var username by remember { mutableStateOf(savedUsername) }
    var password by remember { mutableStateOf(savedPassword) }
    val state by viewModel.state.collectAsState()
    var showDialog by remember {
        mutableStateOf(true)
    }
    state.error?.let {
        CustomDialog(
            showDialog = true,
            onDismissRequest = {
                viewModel.clearState()
            }
        ) {
//            ErrorWarning(
//                onDismissRequest = {  viewModel.clearState()},
//                title = "خطا",
//                message = it
//            )
            ErrorDialog(it, {viewModel.clearState()}, false)
        }
    }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(color = PlatinumSilver),
        ) {

            Column(
                modifier = modifier
                    .padding(10.dp)
                    .fillMaxSize()
                    .align(Alignment.Center)
                ,
                horizontalAlignment = Alignment.CenterHorizontally,

                ) {
                Spacer(modifier = Modifier.height(DIMENS_14dp))
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "logo",
                    modifier = Modifier
                        .size(150.dp, 150.dp)
                        .layoutId("logo")
                )
                Spacer(modifier = Modifier.height(DIMENS_14dp))
                RoundedIconTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = "کد ملی",
                    icon = Icons.Default.Person
                )
                Spacer(modifier = Modifier.height(DIMENS_14dp))
                RoundedIconTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "رمز عبور",
                    icon = Icons.Default.Lock,
                    isPassword = true
                )
                Spacer(modifier = Modifier.height(DIMENS_14dp))
                Text(
                    text = "رمز عبور خود را فراموش کرده اید؟",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(DIMENS_14dp))
//                Button(
//                    onClick = {
//                    viewModel.getToken(username, password)
//                    },
//                    modifier = Modifier.fillMaxWidth(),
//                    colors = ButtonDefaults.buttonColors(containerColor = RoyalRed),
//                    shape = RoundedCornerShape(6.dp)
//                ) {
//                    Text(
//                        "ورود",
//                        style = Typography.labelLarge
//                    )
//                }

                ButtonBorderAnmation(
                    modifier =  Modifier.fillMaxWidth(),
                    "ورود",
                    true,
                    state.isLoading,
                    {
                        viewModel.getToken(username, password)
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    LoginScreen()
}