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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.msa.eshop.R
import com.msa.eshop.data.Model.response.ReportHistoryCustomerModel
import com.msa.eshop.data.local.entity.ProductGroupEntity
import com.msa.eshop.ui.theme.*

@Composable
fun OrderStatusReport(
    modifier: Modifier = Modifier,
    reportHistory: ReportHistoryCustomerModel,
    onClick: (Int) -> Unit,
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Card(
            modifier = Modifier
                .padding(horizontal = 3.dp, vertical = 3.dp)
                .fillMaxWidth()
                .shadow(10.dp, RoundedCornerShape(18.dp)),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {


            Row(
                modifier = modifier
                    .padding(horizontal = 3.dp, vertical = 3.dp)
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

                        Image(
                            painter = painterResource(id = R.drawable.ic_orderstatus),
                            contentDescription = "orderstatus",
                            modifier = Modifier.fillMaxSize(),
                        )
                    }

                    Column(
                        modifier = modifier.background(Color.White)
                    ) {

                        Text(
                            modifier = Modifier
                                .padding(3.dp),
                            text = "سبد شماره :${reportHistory.cartCode.toString()}",
                            style = Typography.titleLarge,
                        )

                        Text(
                            text = reportHistory.date,
                            style = Typography.titleSmall,
                            color = barcolorlow
                        )

                    }

                }
                val color = Color(android.graphics.Color.parseColor(reportHistory.color))
                Box(
                    modifier = Modifier
                        .padding(5.dp)
                        .size(90.dp, 50.dp)
                        .background(
                            color = color, shape = RoundedCornerShape(18.dp)
                        )
                ) {
                    Text(
                        text = reportHistory.status,
                        textAlign = TextAlign.Center,
                        modifier = modifier
                            .align(Alignment.Center),
                        color = Color.White,
                        style = Typography.titleSmall,
                    )
                }
            }
            HorizontalDivider(
                color = barcolorlight,
                thickness = 2.dp,
                modifier = Modifier.padding(vertical = 9.dp)
            )

            Text(
                modifier = modifier
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                text = "آدرس:",
                textAlign = TextAlign.Center,
                style = Typography.titleSmall,
            )

            Text(
                modifier = modifier
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                text = reportHistory.address,
                textAlign = TextAlign.Center,
                style = Typography.titleSmall,
            )
            Row(
                modifier = modifier
                    .padding(horizontal = 4.dp, vertical = 4.dp)
                    .fillMaxWidth()
                    .clickable {
                        onClick(reportHistory.cartCode)
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "جزئیات سفارش",
                    textAlign = TextAlign.Center,
                    style = Typography.labelSmall,
                    color = barcolorlow
                )
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "ArrowBack",
                    modifier = modifier
                        .size(20.dp, 20.dp)
                )
            }

        }
    }

}


@Preview
@PreviewScreenSizes
@Composable
private fun OrderStatusReportPreview() {
    EShopTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            OrderStatusReport(
                reportHistory =
                ReportHistoryCustomerModel(
                    id = "1",
                    customerCode = "C001",
                    customerName = "John Doe",
                    date = "2024-06-09",
                    address = "123 Main St",
                    status = "Delivered",
                    color = "#FF6347",
                    cartCode = 1001
                ),
                onClick = {}
            )
        }

    }

}