package com.msa.eshop.ui.screen.accountInformation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.msa.eshop.ui.component.weightC.RoundedIconTextField

@Preview
@Composable
fun AccountInformationScreen(
    modifier: Modifier = Modifier,
    viewModel:AccountInformationViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
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
            value = user?.customerName.toString(),
            onValueChange = { },
            label = "نام و نام خانوادگی",
            icon = Icons.Outlined.Person,
            modifier = Modifier.padding(3.dp),
            typeEnabled = false
        )
        RoundedIconTextField(
            value = user?.mobile.toString(),
            onValueChange = { },
            label = "شماره تماس",
            icon = Icons.Outlined.Phone,
            modifier = Modifier.padding(6.dp),
            typeEnabled = false
        )
//        RoundedIconTextField(
//            value = "ایمیل",
//            onValueChange = { },
//            label = "ایمیل",
//            icon = Icons.Outlined.Email,
//            modifier = Modifier.padding(6.dp),
//            typeEnabled = false
//        )
        RoundedIconTextField(
            value = user?.center.toString(),
            onValueChange = { },
            label = "شعبه پخش",
            icon = Icons.Outlined.Business,
            modifier = Modifier.padding(6.dp),
            typeEnabled = false
        )
        RoundedIconTextField(
            value = user?.center.toString(),
            onValueChange = { },
            label = "شهر",
            icon = Icons.Outlined.LocationOn,
            modifier = Modifier.padding(6.dp),
            typeEnabled = false
        )
    }
}