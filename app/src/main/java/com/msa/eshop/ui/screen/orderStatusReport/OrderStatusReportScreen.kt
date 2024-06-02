package com.msa.eshop.ui.screen.orderStatusReport

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.msa.eshop.data.Model.response.ReportHistoryCustomerModel

@Composable
fun OrderStatusReportScreen(modifier: Modifier = Modifier) {

    val viewModel: OrderStatusReportViewModel = hiltViewModel()
    val orderStatus by viewModel.orderStatusReport.collectAsState()

    Scaffold(
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            val p = it
            LazyColumn {
                items(orderStatus) { customer ->
                    CustomerItem(customer)
                }
            }
        }
    }
}


@Composable
fun CustomerItem(customer: ReportHistoryCustomerModel) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "Name: ${customer.customerName}")
            Text(text = "Code: ${customer.customerCode}")
            Text(text = "Date: ${customer.date}")
            Text(text = "Address: ${customer.address}")
            Text(text = "Status: ${customer.status}")
        }
    }
}