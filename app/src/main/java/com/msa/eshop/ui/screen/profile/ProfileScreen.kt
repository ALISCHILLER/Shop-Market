package com.msa.eshop.ui.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.msa.eshop.R
import com.msa.eshop.ui.common.topBar.TopBarDetails
import com.msa.eshop.ui.component.expandable.ExpandableCard
import com.msa.eshop.ui.theme.Typography
import com.msa.eshop.ui.theme.barcolorlight2

@Composable
fun ProileScreen(modifier: Modifier = Modifier) {


    Scaffold(
        modifier = modifier
            .background(color = Color.White),
        topBar = {
            TopBarDetails("پروفایل")
        },
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.iprofile),
                        contentDescription = "User Profile Image",
                        modifier = Modifier
                            .size(65.dp)
                            .clip(CircleShape),
//                           .border(width = 1.dp, color = Color.Gray, shape = CircleShape)
                    )
                    Column {
                        Text(
                            text = "نام مشتری",
                            style = Typography.labelSmall
                        )
                        Text(
                            text = "کد مشتری",
                            style = Typography.titleSmall,
                            color = barcolorlight2
                        )
                    }
                }

                Column {
                    ExpandableCard(
                        title = "اطلاعات حساب کاربری"
                    ) {

                    }
                }

            }
        }
    }
}


@Preview
@PreviewScreenSizes
@Composable
private fun ProfilePreview() {
    ProileScreen()
}