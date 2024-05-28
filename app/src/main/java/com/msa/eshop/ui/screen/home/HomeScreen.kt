@file:OptIn(
    ExperimentalPagerApi::class, ExperimentalPagerApi::class, ExperimentalPagerApi::class,
    ExperimentalFoundationApi::class, ExperimentalFoundationApi::class
)

package com.msa.eshop.ui.screen.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.msa.componentcompose.ui.component.lottiefile.LoadingAnimate
import com.msa.eshop.data.local.entity.ProductGroupEntity
import com.msa.eshop.ui.common.card.ProductCard
import com.msa.eshop.ui.common.card.ProductGroupCard
import com.msa.eshop.ui.component.TitleGrouping
import com.msa.eshop.ui.component.graidC.NonlazyGrid
import com.msa.eshop.ui.component.pager.SliderBanner
import com.msa.eshop.ui.theme.PlatinumSilver

@ExperimentalMaterial3Api
@Composable
@Preview
fun HomeScreen(

) {
    val viewModel: HomeViewModel = hiltViewModel()


    val state by viewModel.state.collectAsState()
    val products by viewModel.allProduct.collectAsState()
    val banner by viewModel.banner.collectAsState()
    val allProductGroup by viewModel.allProductGroup.collectAsState()
    var selectedProductGroup by remember { mutableStateOf<ProductGroupEntity?>(null) }
    LaunchedEffect(Unit) {
        viewModel.productCheck()
    }
    val lazyListState = rememberLazyListState()
    var scrolledY by remember { mutableStateOf(0f) }
    var previousOffset by remember { mutableStateOf(0) }
    var refreshing by remember { mutableStateOf(state.isLoading) }



    Scaffold(
        modifier = Modifier.background(color = PlatinumSilver),
        topBar = {
            TopBarSearch()
        }
    ) { innerPadding ->
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {

            SwipeRefresh(
                modifier = Modifier
                    .padding(innerPadding),
                state = rememberSwipeRefreshState(isRefreshing = refreshing),
                onRefresh = { viewModel.refresh() }
            ) {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.White),
                    lazyListState,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        SliderBanner(
                            banner = banner,
                            modifier = Modifier
                                .graphicsLayer {
                                    scrolledY += lazyListState.firstVisibleItemScrollOffset - previousOffset
                                    translationY = scrolledY * 0.5f
                                    scaleX = 1 / ((scrolledY * 0.01f) + 1f)
                                    scaleY = 1 / ((scrolledY * 0.01f) + 1f)
                                    previousOffset = lazyListState.firstVisibleItemScrollOffset
                                }
                        )
                    }


                    stickyHeader {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                itemsIndexed(allProductGroup) { index, productGroup ->
                                    ProductGroupCard(
                                        productGroup,
                                        onClick = {
                                            selectedProductGroup = it
                                        },
                                        isSelected = selectedProductGroup == productGroup
                                    )
                                }
                            }
                        }
                    }

                    items(products.chunked(2)) { rowItems ->

                        Row {
                            rowItems.forEach { product ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                ) {
                                    ProductCard(
                                        product = product,
                                        onClick = {
                                            // عملیات مورد نظر
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LoadingAnimate()
                }

            }
        }
    }
}