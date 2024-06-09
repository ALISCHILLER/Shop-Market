package com.msa.eshop.ui.screen.simulate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.msa.eshop.R
import com.msa.eshop.data.local.entity.PaymentMethodEntity
import com.msa.eshop.ui.common.card.RowText
import com.msa.eshop.ui.common.card.SimulateCard
import com.msa.eshop.ui.common.topBar.TopBarDetails

import com.msa.eshop.ui.theme.PlatinumSilver
import com.msa.eshop.ui.theme.Typography
import com.msa.eshop.utils.Currency

@PreviewScreenSizes
@Composable
fun SimulateScreen(
    modifier: Modifier = Modifier,
    viewModel: SimulateViewModel = hiltViewModel()
) {

    val simulate by viewModel.simulat.collectAsState()
    Scaffold(
        topBar = {
            TopBarDetails("پیش نمایش فاکتور")
        },
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .background(color = PlatinumSilver)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {

                LazyColumn(
                    modifier = Modifier
                        // اینجا از وزن استفاده شده است تا LazyColumn بیشتر از فضای دیگری اشغال کند
                        .fillMaxWidth()
                        .weight(1.0f), // پر کردن عرض موجود در طول
                ) {
                    items(simulate) {
                        SimulateCard(simulate = it)
                    }
                }

                Box(
                    Modifier
                        .padding(vertical = 5.dp, horizontal = 5.dp)
                        .shadow(5.dp)
                        .background(color = MaterialTheme.colors.surface)
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp)
                ) {
                    Column(
                        modifier = modifier
                            .fillMaxWidth(),
                    ){
                        val cashprice = simulate.sumOf { it.priceByDiscountPercentAndTax_immediate }
                        RowText(
                            title = "قیمت نقدی",
                            message ="${Currency(cashprice).toFormattedString()} ریال"
                        )

                        val checkprice = simulate.sumOf { it.priceByDiscountPercentAndTax_cheque }
                        RowText(
                            title = "قیمت چک",
                            message ="${Currency(checkprice).toFormattedString()} ریال"
                        )

                        val receiptprice = simulate.sumOf { it.priceByDiscountPercentAndTax_Receipt }
                        RowText(
                            title = "قیمت عرفی",
                            message ="${Currency(receiptprice).toFormattedString()} ریال"
                        )
                        Row(
                            modifier = modifier
                                .fillMaxWidth(),
                        ){
                            Button(
                                onClick = {

                                    val paymentMethod = PaymentMethodEntity(
                                        cashprice = cashprice.toString(),
                                        checkprice = checkprice.toString(),
                                        receiptprice = receiptprice.toString()
                                    )

                                    viewModel.savePayment(paymentMethod)
                                },
                                modifier = Modifier
                                    .padding(5.dp)
                                    .fillMaxWidth()
                                ,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    stringResource(id = R.string.choess_address),
                                    style = Typography.titleSmall,
                                )
                            }
//                        val totalPrice = simulate.sumOf { it.priceByDiscountPercentAndTax }
//                        Text(
//                            text = "${Currency(totalPrice).toFormattedString()} ریال ",
//                            style = Typography.labelLarge
//                        )
                        }
                    }

                }

            }


        }
    }
}

