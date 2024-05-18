@file:OptIn(ExperimentalPagerApi::class, ExperimentalPagerApi::class)

package com.msa.eshop.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.msa.eshop.data.local.entity.ProductGroupEntity
import com.msa.eshop.ui.common.card.ProductCard
import com.msa.eshop.ui.common.card.ProductGroupCard
import com.msa.eshop.ui.component.TitleGrouping
import com.msa.eshop.ui.component.pager.SliderBanner

@ExperimentalMaterial3Api
@Composable
@Preview
fun HomeScreen(

) {
    val viewModel: HomeViewModel = hiltViewModel()

    LaunchedEffect(Unit){
        viewModel.productCheck()
    }
    viewModel.ProductRequest()
    val products by viewModel.allProduct.collectAsState()
    val allProductGroup by viewModel.allProductGroup.collectAsState()
    var selectedProductGroup by remember { mutableStateOf<ProductGroupEntity?>(null) }

    Scaffold(
        topBar = {
            TopBarSearch()
        }
    ) { innerPadding ->
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
            ) {
                SliderBanner()
                TitleGrouping(titleText = "خرید براساس دسته بندی")

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
//                    items(allProductGroup) { productGroup ->
//                        ProductGroupCard(
//                            productGroup,
//                            onClick = { selectedProductGroup = it },
//                            isSelected = selectedProductGroup == productGroup
//                        )
//                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.0f),
                ) {
                    items(products){product ->

                        ProductCard(
                            product = product,
                            onClick = {

                            }
                        )
                    }
                }

            }
        }
    }
}