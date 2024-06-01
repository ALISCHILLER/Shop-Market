package com.msa.eshop.ui.screen.basket

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.msa.eshop.R
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.ui.common.card.BasketCard
import com.msa.eshop.ui.common.topBar.TopBarDetails
import com.msa.eshop.ui.screen.home.HomeViewModel
import com.msa.eshop.ui.theme.PlatinumSilver
import com.msa.eshop.ui.theme.Typography
import kotlinx.coroutines.delay

@Composable
fun BasketScreen(
    modifier: Modifier = Modifier,
    viewModel: BasketViewModel = hiltViewModel()
) {


    val orders = rememberSaveable { mutableStateOf<List<OrderEntity>>(emptyList()) }
    val counter = remember { mutableStateOf(0) }
    LaunchedEffect(counter.value) {
        orders.value = viewModel.allOrder.value
    }

    LaunchedEffect(Unit) {
        delay(1000)
        orders.value = viewModel.allOrder.value
    }
    Scaffold(
        modifier = Modifier
            .background(color = Color.White),
        topBar = {
            TopBarDetails("لیست خرید های شما")
        },
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .background(color = Color.White)
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
                    items(orders.value) { order ->

                        BasketCard(
                            orderEntity = order,
                            onClick = {
                                counter.value++
                            },

                            )
                    }
                }
                Button(
                    onClick = {
                              viewModel.navigateToSimulate()
                    },
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        stringResource(id = R.string.pre_invoice_registration),
                        style = Typography.titleSmall,
                    )
                }
            }


        }
    }
}
