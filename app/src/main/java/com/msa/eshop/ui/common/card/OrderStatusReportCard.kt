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
import com.msa.eshop.ui.theme.OrangeStatus
import com.msa.eshop.ui.theme.PlatinumSilver
import com.msa.eshop.ui.theme.Typography
import com.msa.eshop.ui.theme.barcolorlight
import com.msa.eshop.ui.theme.barcolorlight2
import com.msa.eshop.utils.Currency

@Composable
fun OrderStatusReport(modifier: Modifier = Modifier) {
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
                                text = "23584",
                                style = Typography.labelSmall,
                                color = barcolorlight2
                            )

                            Text(
                                text = "1403/02/01",
                                style = Typography.titleSmall,
                                color = barcolorlight2
                            )

                        }

                    }

                    Box(
                        modifier = Modifier
                            .padding(5.dp)
                            .size(90.dp, 50.dp)
                            .background(
                                color = OrangeStatus, shape = RoundedCornerShape(18.dp)
                            )
                    ) {
                        Text(
                            text = "ثبت اولیه",
                            textAlign = TextAlign.Center,
                            modifier = modifier
                                .fillMaxSize()
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


            }
        }


    }
}


@Preview
@Composable
private fun OrderStatusReportPreview() {
    OrderStatusReport()
}