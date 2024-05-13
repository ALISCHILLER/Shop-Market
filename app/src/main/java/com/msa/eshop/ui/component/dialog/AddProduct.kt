package com.msa.eshop.ui.component.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.msa.eshop.R
import com.msa.eshop.ui.component.weightC.CounterButton
import com.msa.eshop.ui.theme.PlatinumSilver
import com.msa.eshop.ui.theme.Typography
import com.msa.eshop.utils.Currency

@Preview
@Composable
fun AddProduct(modifier: Modifier = Modifier) {
    var value1 by remember { mutableStateOf( 0) }
    var value2 by remember { mutableStateOf( 0) }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Box(
                    modifier = Modifier
                        .padding(5.dp)
                        .size(100.dp, 75.dp)
                        .background(
                            color = PlatinumSilver,
                            shape = RoundedCornerShape(18.dp)
                        )
                        .aspectRatio(1f)
                ) {

//                AsyncImage(
//                    model = order.productImage,
//                    contentDescription = "productImage",
//                    modifier = Modifier.fillMaxSize(),
//                    error = painterResource(id = R.drawable.nourl)
//                )

                    Image(
                        painter = painterResource(R.drawable.product),
                        contentDescription = stringResource(R.string.image_product),
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Column(
                    modifier = modifier
                        .background(Color.White)
                ){
                    Text(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        text = " وینکرز شکلاتی"
                    )
                    Text(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        text = " قیمت هر عدد: 15000 ریال",
                        style = Typography.labelSmall
                    )

                }
            }
            Row(
                modifier = Modifier
                    .padding(7.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                    Text(
                        text ="بسته ",
                        style = Typography.labelSmall
                    )

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
                        }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(7.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text ="کارتن ",
                    style = Typography.labelSmall
                )

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
                        }
                    )
                }
            }
                Row(
                    modifier = Modifier
                        .padding(7.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ){

                    Text(
                        text = "مبلغ ناخالص:",
                        style = Typography.bodyLarge,
                    )
                    Text(
                        text =  "15.54874",
                        style = Typography.bodyLarge,
                    )
                }

        }
    }
}