package com.msa.eshop.ui.screen.addresses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.msa.eshop.R
import com.msa.eshop.ui.common.card.AddressCard
import com.msa.eshop.ui.common.topBar.TopBarDetails
import com.msa.eshop.ui.theme.Typography

@Composable
@Preview
fun AddressesScreen(modifier: Modifier = Modifier) {

    Scaffold(
        modifier = modifier
            .background(color = Color.White),
        topBar = {
            TopBarDetails("آدرس ها")
        },
    ) {
        val p = it
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
//                    itemsIndexed(address) { index, address ->
//                        AddressCard(
//                            orderAddressModel = address,
//                            onClick = {
//                                orderAddress = it
//                                selectAddress = it.id
//                            },
//                            isSelected = orderAddress == address
//                        )
//                    }
                }


                Button(
                    onClick = {

                    },
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        stringResource(id = R.string.new_address),
                        style = Typography.titleSmall,
                    )
                }
            }
        }
    }
}