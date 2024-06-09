package com.msa.eshop.ui.common.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.msa.eshop.R
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.data.local.entity.ProductModelEntity
import com.msa.eshop.ui.component.dialog.AlertDialogExample
import com.msa.eshop.ui.component.weightC.CounterButton
import com.msa.eshop.ui.component.weightC.CounterButtonNew
import com.msa.eshop.ui.screen.basket.BasketViewModel
import com.msa.eshop.ui.theme.PlatinumSilver
import com.msa.eshop.ui.theme.Typography
import com.msa.eshop.ui.theme.barcolorlight2
import com.msa.eshop.utils.Currency

@Composable
fun BasketCard(
    modifier: Modifier = Modifier,
    orderEntity: OrderEntity,
    onClick: (Boolean) -> Unit,
    viewModel: BasketViewModel = hiltViewModel()
) {
    var value1 by remember { mutableStateOf(orderEntity.numberOrder1) }
    var value2 by remember { mutableStateOf(orderEntity.numberOrder2) }
    var chack by remember { mutableStateOf(false) }
    // تابع برای محاسبه قیمت به‌روز شده

    val totalPrice by remember(value1, value2, orderEntity) {
        mutableStateOf(
            viewModel.calculateTotalPriceAndHandleOrder(
                value1, value2,
                orderEntity
            )
        )
    }



    if (chack)
        AlertDialogExample(
            onConfirmation = {
                viewModel.deleteOrder(orderEntity.id)
                viewModel.getAllOrder()
                onClick(true)
                chack = false
            },
            onDismissRequest = { chack = false }
        )
    if (totalPrice < 1) {
        onClick(true)
    }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(10.dp, RoundedCornerShape(18.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {

            Column(
                modifier = modifier
                    .background(Color.White)
                    .padding(5.dp)
            ) {
                Row(
                    modifier = modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth(),

                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {


                        Box(
                            modifier = Modifier
                                .padding(5.dp)
                                .size(90.dp, 65.dp)
                                .background(
                                    color = PlatinumSilver, shape = RoundedCornerShape(18.dp)
                                )
                                .aspectRatio(1f)
                        ) {

                            AsyncImage(
                                model = orderEntity.productImage,
                                contentDescription = "productImage",
                                modifier = Modifier.fillMaxSize(),
                                error = painterResource(id = R.drawable.not_load_image)
                            )
                        }

                        Column(
                            modifier = modifier.background(Color.White)
                        ) {
                            orderEntity.productName?.let {
                                Text(
                                    modifier = Modifier
                                        .padding(3.dp),
                                    text = it,
                                    style = Typography.labelSmall
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .padding(3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(modifier = Modifier.padding(5.dp))
                                Text(
                                    text = "فی:",
                                    style = Typography.titleSmall
                                )
                                Spacer(modifier = Modifier.padding(5.dp))
                                Text(
                                    text = Currency(orderEntity.price).toFormattedString(),
                                    style = Typography.titleSmall,
                                    color = barcolorlight2
                                )
                                Spacer(modifier = Modifier.padding(5.dp))
                                Text(
                                    text = "ریال ",
                                    style = Typography.titleSmall,
                                    color = barcolorlight2
                                )
                            }

                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.remove_icon),
                            contentDescription = "",
                            modifier = Modifier
                                .padding(5.dp)
                                .size(50.dp, 50.dp)
                                .clickable {
                                    chack = true
                                }
                        )

                    }

                }
                Row(
                    modifier = Modifier
                        .align(alignment = Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    orderEntity.fullNameKala1?.let {
                        Box(
                            modifier = Modifier
                                .weight(1f),
                        ) {
                            Text(
                                text = "$it :",
                                style = Typography.titleSmall
                            )
                        }
                    }
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Box(
                            modifier = Modifier.weight(1f),
                        ) {
                            CounterButtonNew(
                                value = value1.toString(),
                                onValueIncreaseClick = {
                                },
                                onValueDecreaseClick = {
                                },
                                onValueClearClick = {
                                },
                                onValue = { value1 = it.toInt() }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .align(alignment = Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    orderEntity.fullNameKala2?.let {
                        Box(
                            modifier = Modifier
                                .weight(1f),
                        ) {
                            Text(
                                text = "$it :",
                                style = Typography.titleSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Box(
                            modifier = Modifier.weight(1f),
                        ) {
                            CounterButtonNew(
                                value = value2.toString(),
                                onValueIncreaseClick = {
                                },
                                onValueDecreaseClick = {
                                },
                                onValueClearClick = {
                                },
                                onValue = { value2 = it.toInt() }
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .padding(horizontal = 3.dp, vertical = 4.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "مبلغ ناخالص:",
                        style = Typography.titleSmall,
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = Currency(totalPrice).toFormattedString(),
                        style = Typography.titleSmall,
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "ریال ",
                        style = Typography.titleSmall
                    )
                }

            }
        }
    }
}

@Composable
@Preview
fun BasketCardPreview() {
    Column(
        modifier = Modifier
            .background(Color.White)
    ) {
        BasketCard(
            orderEntity = OrderEntity(
                "11",
                convertFactor1 = 1,
                convertFactor2 = 12,
                fullNameKala1 = "عدد(1)",
                fullNameKala2 = "کارتن(30)",
                productCode = 659985,
                productGroupCode = 54544,
                productName = "biscuit",
                unit1 = "shelf",
                unit2 = "Carton",
                unitid1 = "54654",
                unitid2 = "4565",
                price = 98563,
                productImage = "",
                numberOrder1 = 44,
                unitOrder = "",
                numberOrder2 = 55,
                numberOrder = 632552
            ),
            onClick = {}
        )
    }

}