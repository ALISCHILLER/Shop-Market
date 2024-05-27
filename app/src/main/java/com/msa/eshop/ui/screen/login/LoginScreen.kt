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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.msa.componentcompose.ui.component.lottiefile.AnimatedPreloader
import com.msa.componentcompose.ui.component.lottiefile.LoadingAnimate
import com.msa.eshop.R
import com.msa.eshop.ui.component.button.ButtonBorderAnmation
import com.msa.eshop.ui.component.dialog.CustomDialog
import com.msa.eshop.ui.component.dialog.ErrorDialog
import com.msa.eshop.ui.component.dialog.ErrorWarning
import com.msa.eshop.ui.component.drawLineC.BezierCurve
import com.msa.eshop.ui.component.drawLineC.BezierCurveStyle
import com.msa.eshop.ui.component.loading.LoadingAnimation
import com.msa.eshop.ui.component.weightC.RoundedIconTextField
import com.msa.eshop.ui.theme.DIMENS_14dp
import com.msa.eshop.ui.theme.DIMENS_6dp
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
            ErrorWarning(
                onDismissRequest = { viewModel.clearState() },
                title = "خطا",
                message = it
            )
            ErrorDialog(it, { viewModel.clearState() }, false)
        }
    }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val stroke1Dp = with(LocalDensity.current) { 1.dp.toPx() }
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(color = PlatinumSilver),
        ) {

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                BezierCurve(
                    modifier = Modifier
                        .fillMaxWidth()
                        .rotate(180f)
                        .height(100.dp),
                    points = listOf(30F, 60F, 40f, 100F, 50F),
                    minPoint = 0F,
                    maxPoint = 100F,
                    style = BezierCurveStyle.StrokeAndFill(
                        strokeBrush = Brush.horizontalGradient(listOf(Color.Red, Color.Red)),
                        fillBrush = Brush.verticalGradient(
                            listOf(
                                Color(0xFFB9081F),
                                Color(0xFFE3152F)
                            )
                        ),
                        stroke = Stroke(width = stroke1Dp)
                    ),
                )
                Column(
                    modifier = modifier
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(DIMENS_6dp))
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "logo",
                        modifier = Modifier
                            .size(170.dp, 170.dp)
                            .layoutId("logo")
                    )
                    Spacer(modifier = Modifier.height(DIMENS_14dp))
                    RoundedIconTextField(
                        modifier = modifier,
                        value = username,
                        onValueChange = {
                            username = it
                        },
                        label = "کد مشتزی",
                        icon = Icons.Default.Person,
                        corner = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(DIMENS_14dp))
                    RoundedIconTextField(
                        value = password,
                        onValueChange = {
                            password = it
                        },
                        label = "رمز عبور",
                        icon = Icons.Default.Lock,
                        isPassword = true,
                        corner = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(DIMENS_14dp))
                    Text(
                        text = "رمز عبور خود را فراموش کرده اید؟",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            viewModel.tokenRequest(username, password)
                        },
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            stringResource(id = R.string.login),
                            style = Typography.titleSmall,
                        )
                    }
                }


                BezierCurve(
                    modifier = Modifier
                        .width(200.dp)
                        .rotate(90f)
                        .align(Alignment.End)
                        .height(200.dp),
                    points = listOf(0f, 40f, 30F, 80f),
                    minPoint = 0F,
                    maxPoint = 100F,
                    style = BezierCurveStyle.StrokeAndFill(
                        strokeBrush = Brush.horizontalGradient(listOf(Color.Red, Color.Red)),
                        fillBrush = Brush.verticalGradient(
                            listOf(
                                Color(0xFFB9081F),
                                Color(0xFFE3152F)
                            )
                        ),
                        stroke = Stroke(width = stroke1Dp)
                    ),
                )
            }

            if (state.isLoading) {
                LoadingAnimate()
                // Blurred background
            }

        }
    }
}

@Preview
@PreviewScreenSizes
@PreviewFontScale
@Composable
private fun LoginScreenPreview() {
    LoginScreen()
}