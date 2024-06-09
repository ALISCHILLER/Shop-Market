package com.msa.eshop.ui.common.card

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.msa.eshop.R
import com.msa.eshop.data.Model.response.ReportCartDetailsModel
import com.msa.eshop.ui.theme.PlatinumSilver
import com.msa.eshop.ui.theme.RedMain
import com.msa.eshop.ui.theme.Typography
import com.msa.eshop.ui.theme.barcolorlight
import com.msa.eshop.utils.Currency

@Composable
fun OrderDetailsReportCard(
    modifier: Modifier = Modifier,
    reportCart: ReportCartDetailsModel
) {


    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Card(
            modifier = Modifier
                .padding(horizontal = 3.dp, vertical = 3.dp)
                .fillMaxWidth()
                .shadow(10.dp, RoundedCornerShape(18.dp))
                ,
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
                Row(
                    modifier = modifier
                        .padding(horizontal = 3.dp, vertical = 3.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .padding(5.dp)
                            .size(70.dp, 70.dp)
                            .background(
                                color = PlatinumSilver, shape = RoundedCornerShape(18.dp)
                            )
                            .aspectRatio(1f)
                    ) {
                        AsyncImage(
                            model = reportCart.productImageUrl,
                            contentDescription = "productImage",
                            modifier = Modifier.fillMaxSize(),
                            error = painterResource(id = R.drawable.not_load_image)
                        )
                    }
                    Text(
                        text = reportCart.productName,
                        textAlign = TextAlign.Center,
                        style = Typography.titleSmall,
                    )

                }

                Row(
                    modifier = modifier
                        .padding(horizontal = 5.dp, vertical = 4.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "تعداد سفارش",
                        textAlign = TextAlign.Center,
                        style = Typography.titleSmall,
                    )

                    Text(
                        text = reportCart.quantity.toString(),
                        textAlign = TextAlign.Center,
                        style = Typography.titleSmall,
                    )
                }

                HorizontalDivider(
                    color = barcolorlight,
                    thickness = 2.dp,
                    modifier = Modifier.padding(vertical = 9.dp)
                )
                Row(
                    modifier = modifier
                        .padding(horizontal = 6.dp, vertical = 5.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "جمع کل: ",
                        textAlign = TextAlign.Center,
                        style = Typography.titleSmall,
                    )

                    Row {
                        Text(
                            text = Currency(reportCart.total).toFormattedString(),
                            textAlign = TextAlign.Center,
                            style = Typography.titleSmall,
                            color = RedMain
                        )
                        Text(
                            text = " تومان",
                            textAlign = TextAlign.Center,
                            style = Typography.titleSmall,
                            color = RedMain
                        )
                    }

                }
            }
    }
}

@Preview
@Composable
private fun preview() {
    OrderDetailsReportCard(
        reportCart = ReportCartDetailsModel(
            cartCode = 101,
            customerAddress = "123 Main St, City, Country",
            customerName = "John Doe",
            discount = 10,
            id = "1",
            price = 100,
            productCode = "P001",
            productImageUrl = "http://example.com/image1.jpg",
            productName = "Product 1",
            quantity = 2,
            salesDate = "2023-06-01",
            statusName = "Delivered",
            tax = 5,
            total = 15200
        )
    )
}