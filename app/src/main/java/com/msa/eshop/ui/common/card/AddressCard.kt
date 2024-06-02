package com.msa.eshop.ui.common.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.msa.eshop.R
import com.msa.eshop.data.Model.response.OrderAddressModel
import com.msa.eshop.ui.theme.RedMain
import com.msa.eshop.ui.theme.Typography
import com.msa.eshop.ui.theme.barcolorlight
import com.msa.eshop.ui.theme.barcolorlight2

@Composable
fun AddressCard(
    modifier: Modifier = Modifier,
    orderAddressModel: OrderAddressModel,
    isSelected: Boolean,
    onClick: (OrderAddressModel) -> Unit,
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {

        Box(
        ){



        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(10.dp, RoundedCornerShape(18.dp))
                .clickable {
                    onClick(orderAddressModel)
                }
                .padding(horizontal = 10.dp, vertical = 5.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            border = if(isSelected) BorderStroke(1.dp, RedMain) else BorderStroke(1.dp, barcolorlight)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(18.dp)
                    )
            ) {

                Column(
                    modifier = modifier
                        .padding(5.dp)
                ) {
                    Row(
                        modifier = modifier.fillMaxWidth(),

                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 5.dp, vertical = 13.dp)
                            ,
                            contentAlignment = Alignment.TopStart
                        ) {
                            Column {
                                Text(
                                    text = orderAddressModel.centerName,
                                    style = Typography.bodyLarge
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 2.dp, vertical = 13.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row {
                                Text(
                                    text = "شماره تماس :",
                                    style = Typography.bodyLarge
                                )

                                Text(
                                    text = orderAddressModel.customerMobile,
                                    style = Typography.titleSmall,
                                    color = barcolorlight2
                                )
                            }
                        }

                        Box(
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp),
                            contentAlignment = Alignment.TopEnd,
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_menu),
                                contentDescription =""
                            )
                        }


                    }
                    HorizontalDivider(
                        color = barcolorlight,
                        thickness = 2.dp,
                        modifier = Modifier.padding(vertical = 9.dp)
                    )
                    Text(
                        text = orderAddressModel.customerAddress,
                        style = Typography.titleSmall,
                        maxLines = 2
                    )
                }

            }

        }


            Box(
                modifier = Modifier
                    .alpha(if(isSelected) 100f else 0f)
                    .align(Alignment.TopStart)
                    .clickable {
                    },
                contentAlignment = Alignment.TopStart
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd),
                ) {
                    Image( modifier = modifier
                        .size(25.dp),
                        painter = painterResource(id = R.drawable.tick_circle),
                        contentDescription = "icon discount",
                    )

                }
            }
    }
    }

}


@Preview
@PreviewScreenSizes
@Composable
private fun AddressCardPreview() {

    Column(
        modifier = Modifier
            .background(Color.White)
    ) {
        AddressCard(
         orderAddressModel =   OrderAddressModel(
                customerAddress = "کرج، گوهردشت، میدان مادر، سنبل شمالی، پلاک 90، ویلایی",
                customerPhone = "09335385974",
                centerName = "تهران",
                customerMobile = "09335385974",
                id = "1",
            ),
            isSelected = true,
            onClick={}
        )

    }
}