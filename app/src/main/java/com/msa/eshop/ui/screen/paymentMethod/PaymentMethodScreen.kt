package com.msa.eshop.ui.screen.paymentMethod

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.msa.eshop.R
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.ui.common.topBar.TopBarDetails
import com.msa.eshop.ui.component.radioC.RadioGroupColumn
import com.msa.eshop.ui.screen.home.HomeViewModel
import com.msa.eshop.ui.theme.Typography
import com.msa.eshop.ui.theme.barcolorlight
import com.msa.eshop.ui.theme.barcolorlight2
import com.msa.eshop.utils.Currency

@Composable
fun PaymentMethodScreen(modifier: Modifier = Modifier) {
    val viewModel: PaymentMethodViewModel = hiltViewModel()
    val orders = rememberSaveable { mutableStateOf<List<OrderEntity>>(emptyList()) }
    LaunchedEffect(Unit) {
        viewModel.getOrderAddress()
        viewModel.getAllPaymentMethod()
    }
    val state by viewModel.state.collectAsState()
    val orderAddress by viewModel.orderAddress.collectAsState()
    val paymentMethod by viewModel.paymentMethod.collectAsState()
    Scaffold(
        modifier = Modifier
            .background(color = Color.White),
        topBar = {
            TopBarDetails(stringResource(id = R.string.payment_method))
        },
    ) {
        val p = it
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .background(color = Color.White)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceAround
            ) {

                if (orderAddress.isNotEmpty()) {

                    orderAddress.get(0).customerAddress?.let { it1 ->
                        RowText(
                            title = "آدرس",
                            message = it1
                        )
                    }
                    RowText(
                        title = "شماره تماس",
                        message = orderAddress.get(0).customerMobile.toString()
                    )
                }


                RowText(
                    title = "تاریخ ارسال",
                    message = "1403/02/03"
                )

                HorizontalDivider(color = barcolorlight, thickness = 2.dp)

                var selectedOption by remember { mutableStateOf("عرفی") }

                if(paymentMethod.isNotEmpty())
                RadioGroupColumn(
                    options = listOf(
                        " قیمت نقدی: ${paymentMethod.get(0).cashprice?.let { it1 -> Currency(it1) }} ریال",
                        " قیمت چک: ${paymentMethod.get(0).checkprice?.let { it1 -> Currency(it1) }} ریال",
                        " قیمت عرفی: ${paymentMethod.get(0).receiptprice?.let { it1 -> Currency(it1) }} ریال",
                    ),
                    selectedOption = selectedOption,
                    onOptionSelected = { selectedOption = it }
                )

                HorizontalDivider(color = barcolorlight, thickness = 2.dp)

                var selectedOptio by remember { mutableStateOf("پرداخت به صورت نقدی") }
                RadioGroupColumn(
                    options = listOf("پرداخت به صورت نقدی", "پرداخت در محل"),
                    selectedOption = selectedOptio,
                    onOptionSelected = { selectedOptio = it }
                )

                Button(
                    onClick = {
                        viewModel.getOrderToSimulate(orderAddress.get(0).id)
                    },
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        stringResource(id = R.string.the_payment),
                        style = Typography.titleSmall,
                    )
                }

            }

        }
    }


}


@Composable
fun RowText(
    modifier: Modifier = Modifier,
    title: String,
    message: String
) {
    Row(
        modifier = modifier.padding(horizontal = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        Text(
            text = "${title} :",
            style = Typography.titleSmall
        )
        Spacer(modifier = modifier.width(8.dp))
        Text(
            text = message,
            style = Typography.titleSmall,
            color = barcolorlight2
        )
    }
}

@Preview
@Composable
private fun preview() {

    PaymentMethodScreen()
}