package com.msa.eshop.ui.screen.orderDetailsReport

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.msa.eshop.ui.common.card.OrderDetailsReportCard
import com.msa.eshop.ui.common.card.OrderStatusReport
import com.msa.eshop.ui.common.topBar.TopBarDetails
import com.msa.eshop.ui.navigation.NavManager
import com.msa.eshop.ui.screen.orderStatusReport.OrderStatusReportViewModel
import kotlinx.coroutines.delay

@Composable
fun OrderDetailsReportScreen(
    modifier: Modifier = Modifier,
    card: Int? = 0
) {
    val viewModel: OrderDetailsReportViewModel = hiltViewModel()

    val navManager = NavManager()
    val reportInfo by navManager.routeInfo.collectAsState()
    val cardNumber = reportInfo.args?.getInt("card")


    LaunchedEffect(Unit) {
        card?.let { viewModel.reportCartDetailsRequest(it) }
    }
    val orderStatus by viewModel.reportCartDetails.collectAsState()
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
                        OrderDetailsReportCard(reportCart = order)
                    }
                }

            }
        }
    }
}