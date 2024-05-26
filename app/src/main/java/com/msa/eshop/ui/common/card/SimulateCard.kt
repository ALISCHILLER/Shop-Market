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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.msa.eshop.R
import com.msa.eshop.data.Model.SimulateModel
import com.msa.eshop.data.Model.SimulateResultModel
import com.msa.eshop.ui.screen.simulate.SimulateViewModel
import com.msa.eshop.ui.theme.PlatinumSilver
import com.msa.eshop.ui.theme.Typography
import com.msa.eshop.utils.Currency

@Preview
@Composable
private fun preview() {
    val simulateModel1 = SimulateModel(
        convertFactor1 = 1,
        convertFactor2 = 2,
        discountPercent = 10,
        finalPrice = 1000,
        finalPriceDiscount = 900,
        fullNameKala1 = "کالای اول",
        fullNameKala2 = "کالای دوم",
        id = "1",
        isTax = true,
        price = 1100,
        priceByDiscountPercent = 1000,
        priceByDiscountPercentAndTax = 1180,
        priceDiscount = 100,
        productCode = 101,
        quantity = 10,
        productGroupCode = 201,
        productImage = "image1.png",
        productName = "محصول اول",
        unit1 = "واحد اول",
        unit2 = "واحد دوم",
        unitid1 = "u1",
        unitid2 = "u2"
    )
    SimulateCard(simulate =simulateModel1)
}
@Composable
fun SimulateCard(
    modifier: Modifier = Modifier,
    simulate : SimulateModel
) {


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

                        AsyncImage(
                            model = simulate.productImage,
                            contentDescription = "productImage",
                            modifier = Modifier.fillMaxSize(),
                            error = painterResource(id = R.drawable.not_load_image)
                        )
//                        Image(
//                            painter = painterResource(id = R.drawable.product),
//                            contentDescription = "product",
//                            modifier = Modifier.fillMaxSize(),
//                        )
                    }

                    Column(
                        modifier = modifier.background(Color.White)
                    ) {

                            Text(
                                modifier = Modifier
                                    .padding(8.dp),
                                text = simulate.productName,
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
                                text = simulate.price.toString(),
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

                RowText(
                    title = "کدکالا:",
                    message = simulate.productCode.toString()
                )
                RowText(
                    title = "تعداد سفارش",
                    message = "123 کارتن و 7 عدد"
                )

                RowText(
                    title = "تخفیفات",
                    message = Currency(simulate.priceDiscount).toFormattedString()
                )

                RowText(
                    title = "مالیات",
                    message = Currency(simulate.priceByDiscountPercentAndTax).toFormattedString()
                )
                HorizontalDivider(color = Color.Gray, thickness = 2.dp)

                RowText(
                    title = "مبلغ قابل پرداخت",
                    message = Currency(simulate.finalPrice).toFormattedString()
                )
            }
        }
    }

}


@Composable
fun RowText(
    modifier: Modifier = Modifier,
    title:String,
    message:String
) {
    Row(verticalAlignment = Alignment.CenterVertically){
        Text(
            text = "${title} :",
            style = Typography.titleLarge
        )
        Spacer(modifier = modifier.width(8.dp))
        Text(
            text = message,
            style = Typography.labelSmall
        )
    }
}