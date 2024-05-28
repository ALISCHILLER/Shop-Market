package com.msa.eshop.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.msa.eshop.R
import com.msa.eshop.ui.component.DockedSearch

@Composable
@Preview
fun TopBarSearch(modifier: Modifier = Modifier) {
    val viewModel: HomeViewModel = hiltViewModel()
    Column(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth()
            .background(Color.White)
        ,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "logo",
            modifier = Modifier
                .size(150.dp, 50.dp)
                .layoutId("logo")
        )
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            DockedSearch(
                { viewModel.searchProduct(it) }
            )
        }
    }
}