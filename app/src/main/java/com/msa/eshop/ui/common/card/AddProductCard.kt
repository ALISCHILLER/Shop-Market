package com.msa.eshop.ui.common.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.msa.eshop.R
import com.msa.eshop.data.local.entity.ProductModelEntity
import com.msa.eshop.ui.component.weightC.CounterButton
import com.msa.eshop.ui.screen.home.HomeViewModel
import com.msa.eshop.ui.theme.PlatinumSilver
import com.msa.eshop.ui.theme.Typography
import com.msa.eshop.utils.Currency


@Composable
fun AddProduct(
    modifier: Modifier = Modifier,
    product: ProductModelEntity,
    onDismissRequest: () -> Unit,
) {
    val viewModel: HomeViewModel = hiltViewModel()
    LaunchedEffect(Unit){
        viewModel.productCheck()
    }

    val allOrder by viewModel.allOrder.collectAsState()
    val orderItem = allOrder.firstOrNull { it.id == product.id }

    var value1 by remember { mutableStateOf(orderItem?.numberOrder1 ?: 0) }
    var value2 by remember { mutableStateOf(orderItem?.numberOrder2 ?: 0) }

    // تابع برای محاسبه قیمت به‌روز شده
    val totalPrice by remember(value1, value2, product) {
        mutableStateOf(viewModel.calculateTotalPriceAndHandleOrder(value1, value2, product))
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier
                .background(Color.White)
                .padding(17.dp)
        ) {
            Row(
                modifier = modifier.padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Box(
                    modifier = Modifier
                        .padding(5.dp)
                        .size(100.dp, 75.dp)
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
                Column(
                    modifier = modifier.background(Color.White)
                ) {
                    product.productName?.let {
                        Text(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(), text = it
                        )
                    }
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.padding(5.dp))
                        Text(
                            text = "فی:",
                            style = Typography.titleLarge
                        )
                        Spacer(modifier = Modifier.padding(5.dp))
                        Text(
                            text = Currency(product.price).toFormattedString(),
                            style = Typography.labelLarge,
                        )
                        Spacer(modifier = Modifier.padding(5.dp))
                        Text(
                            text = "ریال ",
                            style = Typography.titleLarge
                        )
                    }

                }
            }
            Row(
                modifier = Modifier
                    .padding(7.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                product.fullNameKala1?.let {
                    Text(
                        text = "$it :", style = Typography.labelSmall
                    )
                }

                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    CounterButton(
                        value = value1.toString(),
                        onValueIncreaseClick = {
                            value1 += 1
                        },
                        onValueDecreaseClick = {
                            value1 = maxOf(value1 - 1, 0)
                        },
                        onValueClearClick = {
                            value1 = 0
                        })
                }
            }

            Row(
                modifier = Modifier
                    .padding(7.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                product.fullNameKala2?.let {
                    Text(
                        text = "$it :", style = Typography.labelSmall
                    )
                }

                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    CounterButton(
                        value = value2.toString(),
                        onValueIncreaseClick = {
                            value2 += 1
                        },
                        onValueDecreaseClick = {
                            value2 = maxOf(value2 - 1, 0)
                        },
                        onValueClearClick = {
                            value2 = 0
                        })
                }
            }
            Row(
                modifier = Modifier
                    .padding(7.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "مبلغ ناخالص:",
                    style = Typography.titleLarge,
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = Currency(totalPrice.toString()).toFormattedString(),
                    style = Typography.labelLarge,
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "ریال ",
                    style = Typography.titleLarge
                )
            }

            Button(
                onClick = {
                          onDismissRequest()
                },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    stringResource(id = R.string.save_Order),
                    style = Typography.titleSmall,
                )
            }

        }
    }
}

@Composable
@Preview
fun AddProductPreview() {
    AddProduct(
        product = ProductModelEntity(
            "11",
            convertFactor1 = 1,
            convertFactor2 = 12,
            fullNameKala1 = "biscuit (1)",
            fullNameKala2 = "biscuit (2)",
            productCode = 659985,
            productGroupCode = 54544,
            productName = "biscuit",
            unit1 = "shelf",
            unit2 = "Carton",
            unitid1 = "54654",
            unitid2 = "4565",
            price = 98563,
             isDiscounts = true,
            productImage = ""
        ),
        onDismissRequest = {}
    )
}