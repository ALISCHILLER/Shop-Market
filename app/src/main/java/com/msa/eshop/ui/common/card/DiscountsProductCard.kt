package com.msa.eshop.ui.common.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.msa.eshop.R
import com.msa.eshop.data.local.entity.ProductModelEntity
import com.msa.eshop.ui.screen.home.HomeViewModel
import com.msa.eshop.ui.theme.PlatinumSilver
import com.msa.eshop.ui.theme.RedMain
import com.msa.eshop.ui.theme.Typography
import com.msa.eshop.ui.theme.barcolorlight

@Composable
fun DiscountsProductCard(
    modifier: Modifier = Modifier,
    product: ProductModelEntity,
    onDismissRequest: () -> Unit,
) {

    val viewModel: HomeViewModel = hiltViewModel()
    LaunchedEffect(Unit) {
        viewModel.discountRequest(product.id.toString())
    }

    val discount by viewModel.discount.collectAsState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier
                .background(Color.White)
                .padding(17.dp)
        ) {
            LazyColumn {
                itemsIndexed(discount) { index, item ->
                    Column {


                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp, horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                            ) {
                            Box(
                                modifier = Modifier
                                    .padding(5.dp)
                                    .size(50.dp, 50.dp)
                                    .background(
                                        color = PlatinumSilver, shape = RoundedCornerShape(18.dp)
                                    )
                                    .aspectRatio(1f)
                            ) {

                                AsyncImage(
                                    model = product.productImage,
                                    contentDescription = "productImage",
                                    modifier = Modifier.fillMaxSize(),
                                    error = painterResource(id = R.drawable.not_load_image)
                                )
                            }
                            Spacer(modifier = modifier.width(8.dp))
                            Text(
                                text = stringResource(
                                    id = R.string.discounts_list_data,
                                    item.fromNumber,
                                    item.endNumber
                                ),
                                modifier = Modifier.padding(end = 5.dp),
                                style = Typography.labelSmall
                            )
                            Text(
                                text =  "${item.discountPercent}%",
                                modifier = Modifier.padding(end = 5.dp),
                                style = Typography.labelSmall,
                                color = RedMain
                            )

                            Text(
                                text =  "تخفیف",
                                modifier = Modifier.padding(end = 5.dp),
                                style = Typography.labelSmall
                            )

                        }
                        HorizontalDivider(
                            color = barcolorlight,
                            thickness = 2.dp,
                            modifier = Modifier.padding(vertical = 9.dp)
                        )
                    }
                }
            }
        }
    }

}