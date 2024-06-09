package com.msa.eshop.ui.screen.orderStatusReport

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.msa.eshop.data.Model.response.ReportHistoryCustomerModel
import com.msa.eshop.ui.common.card.OrderStatusReport
import com.msa.eshop.ui.common.topBar.TopBarDetails

@Composable
fun OrderStatusReportScreen(modifier: Modifier = Modifier) {

    val viewModel: OrderStatusReportViewModel = hiltViewModel()
    val orderStatus by viewModel.orderStatusReport.collectAsState()

    Scaffold(
        modifier = Modifier
            .background(color = Color.White),
        topBar = {
            TopBarDetails("وضعیت سفارشات")
        },
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .background(color = Color.White)
                    .fillMaxSize(),
            ) {

                LazyColumn {
                    items(orderStatus) { order ->
                        OrderStatusReport(
                            reportHistory = order,
                            onClick = {cartCode ->
                                viewModel.navigateOrderDetailsReport(cartCode)
                            }

                        )
                    }
                }

            }
        }
    }
}
