package com.msa.eshop.ui.screen.address

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.msa.componentcompose.ui.component.lottiefile.LoadingAnimate
import com.msa.eshop.R
import com.msa.eshop.data.Model.response.OrderAddressModel
import com.msa.eshop.ui.common.card.AddressCard
import com.msa.eshop.ui.common.topBar.TopBarDetails
import com.msa.eshop.ui.component.dialog.ErrorDialog
import com.msa.eshop.ui.theme.Typography
import com.msa.eshop.ui.theme.barcolorlight2

@Composable
fun OrderAddressScreen(modifier: Modifier = Modifier) {

    val viewModel: OrderAddressViewModel = hiltViewModel()
    val address by viewModel.orderAddress.collectAsState()
    var orderAddress by remember { mutableStateOf<OrderAddressModel?>(null) }
    var selectAddress by remember { mutableStateOf<String>("") }
    val state by viewModel.state.collectAsState()

    state.error?.let {
        ErrorDialog(it, {viewModel.clearState()}, false)
    }
    Scaffold(
        modifier = Modifier
            .background(color = Color.White),
        topBar = {
            TopBarDetails("انتخاب آدرس")
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
                    itemsIndexed(address) { index, address ->
                        AddressCard(
                            orderAddressModel = address,
                            onClick = {
                                orderAddress = it
                                selectAddress = it.id
                            },
                            isSelected = orderAddress == address
                        )
                    }
                }



                Box(
                    Modifier
                        .padding(vertical = 5.dp, horizontal = 5.dp)
                        .shadow(5.dp)
                        .background(color = MaterialTheme.colors.surface)
                        .height(64.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp)
                ) {
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 3.dp, vertical = 3.dp),
                            contentAlignment = Alignment.TopStart
                        ) {
                            Button(
                                onClick = {
                                    viewModel.getOrderToInsertCart(selectAddress)
                                },
                                modifier = Modifier
                                    .padding(5.dp)
                                    .fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    stringResource(id = R.string.the_payment),
                                    style = Typography.titleSmall,
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 3.dp, vertical = 3.dp),
                            contentAlignment = Alignment.TopStart
                        ) {
                            Button(
                                onClick = {
                                },
                                modifier = Modifier
                                    .padding(5.dp)
                                    .fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, color = barcolorlight2)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "LocationOn",
                                    tint = barcolorlight2
                                )
                                Text(
                                    stringResource(id = R.string.new_address),
                                    style = Typography.titleSmall,
                                    color = barcolorlight2
                                )
                            }
                        }

                    }
                }
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LoadingAnimate()
                }

            }
        }
    }
}

@Preview
@PreviewScreenSizes
@Composable
private fun AddressPreview() {

    OrderAddressScreen()

}