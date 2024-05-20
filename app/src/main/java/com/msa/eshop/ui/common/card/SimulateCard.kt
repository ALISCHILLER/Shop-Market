package com.msa.eshop.ui.common.card

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.msa.eshop.R
import com.msa.eshop.ui.theme.PlatinumSilver
import com.msa.eshop.ui.theme.Typography
import com.msa.eshop.utils.Currency

@Preview
@Composable
fun SimulateCard(modifier: Modifier = Modifier) {

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = modifier
                    .background(Color.White)
                    .padding(17.dp)
            ) {

                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),

                    verticalAlignment = Alignment.CenterVertically
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

//                        AsyncImage(
//                            model = orderEntity.productImage,
//                            contentDescription = "productImage",
//                            modifier = Modifier.fillMaxSize(),
//                            error = painterResource(id = R.drawable.not_load_image)
//                        )
                        Image(
                            painter = painterResource(id = R.drawable.product),
                            contentDescription = "product",
                            modifier = Modifier.fillMaxSize(),
                        )
                    }

                    Column(
                        modifier = modifier.background(Color.White)
                    ) {

                            Text(
                                modifier = Modifier
                                    .padding(8.dp),
                                text = "تریکرز شکلاتی",
                                style = Typography.labelLarge
                            )

                        Row(
                            modifier = Modifier
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.padding(5.dp))
                            Text(
                                text = "فی:",
                                style = Typography.titleLarge
                            )
                            Spacer(modifier = Modifier.padding(5.dp))
                            Text(
                                text ="150.234",
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


            }
        }
    }

}