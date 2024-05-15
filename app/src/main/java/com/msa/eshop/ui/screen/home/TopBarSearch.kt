package com.msa.eshop.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.msa.eshop.ui.component.DockedSearch

@Composable
@Preview
fun TopBarSearch(modifier: Modifier = Modifier) {
    val viewModel: HomeViewModel = hiltViewModel()
    Column(
        modifier = Modifier
            .padding(top = 7.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "Zar Market",
           color = Color.Red
        )
        DockedSearch(
            {viewModel.searchProduct(it)}
        )
    }
}