package com.msa.eshop.ui.common.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.msa.eshop.data.local.entity.ProductModelEntity
import com.msa.eshop.ui.screen.home.HomeViewModel

@Composable
fun DiscountsProductCard(
    modifier: Modifier = Modifier,
    product: ProductModelEntity,
    onDismissRequest: () -> Unit,
) {

    val viewModel: HomeViewModel = hiltViewModel()
    LaunchedEffect(Unit){
        viewModel.discountRequest(product.productCode.toString())
    }

    val discount by viewModel.discount.collectAsState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier
                .background(Color.White)
                .padding(17.dp)
        ) {
            LazyColumn {
                items(discount) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 20.dp)
                    ) {
                        Text(
                            text = "flag",
                            modifier = Modifier.padding(end = 20.dp)
                        )
                        Text(text = "country")
                    }
                }
            }
        }
    }

}