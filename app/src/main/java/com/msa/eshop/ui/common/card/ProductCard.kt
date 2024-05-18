@file:OptIn(ExperimentalMaterial3Api::class)

package com.msa.eshop.ui.common.card


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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.msa.eshop.R
import com.msa.eshop.data.local.entity.ProductModelEntity
import com.msa.eshop.ui.component.bottomSheetC.BottomSheetExample
import com.msa.eshop.ui.theme.*
import com.msa.eshop.utils.Currency

@Composable
fun ProductCard(
    modifier: Modifier = Modifier,
    product: ProductModelEntity,
    onClick: (ProductModelEntity) -> Unit
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        BottomSheetExample(
            onDismissRequest = {showBottomSheet = false}
        ) {
            AddProduct(
                product = product,
                onDismissRequest = {showBottomSheet = false}
            )
        }
    }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .padding(10.dp)
        ) {

            Surface(
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(1.dp),
                color = Color.White
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = PlatinumSilver,
                                shape = RoundedCornerShape(18.dp)
                            )
                            .aspectRatio(1f)
                            .size(120.dp) // تعیین ارتفاع و عرض ثابت
                    ) {

                        AsyncImage(
                            model = product.productImage,
                            contentDescription = "productImage",
                            modifier = Modifier.fillMaxSize(),
                            error = painterResource(id = R.drawable.not_load_image)
                        )

                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(Color.Red, shape = RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center

                        ) {
                            Row(

                                horizontalArrangement = Arrangement.SpaceAround
                            ){
                                Text(
                                    text = Currency(product.price)
                                        .toFormattedString(),
                                    color = Color.White,
                                    style = Typography.titleLarge
                                )
                                Spacer(modifier = Modifier.padding(5.dp))
                                Text(
                                    text = "ریال ",
                                    color = Color.White,
                                    style = Typography.titleLarge
                                )
                            }


                        }

                    }


                    product.productName?.let {
                        Text(
                            text = it,
                            modifier = Modifier
                                .padding(vertical = 8.dp),
                            maxLines = 1,
                            style = Typography.bodyLarge

                        )
                    }



                    Column(modifier = Modifier.padding(5.dp)) {
                        Row(
                            modifier = Modifier
                                .padding(vertical = 3.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Row() {
                                Text(
                                    text = Currency(product.price).toFormattedString(),
                                    style = Typography.labelLarge,
                                )
                                Spacer(modifier = Modifier.padding(5.dp))
                                Text(
                                    text = "ریال ",
                                    style = Typography.titleLarge
                                )
                            }


                            Text(
                                text = Currency(product.priceByDiscountPercent).toFormattedString(),
                                style = TextStyle(textDecoration = TextDecoration.LineThrough),
                                fontFamily = EShopFontFamily
                            )


                        }

                        Button(
                            onClick = {
                                showBottomSheet = true
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddShoppingCart,
                                contentDescription = "iconbutton"
                            )
                            Spacer(modifier = Modifier.width(DIMENS_8dp))
                            Text(
                                stringResource(id = R.string.add_to_cart),
                                style = Typography.labelSmall,
                            )
                        }
                    }


                }
            }
        }
    }

}

@Preview
@Composable
private fun ProductCardPreciew() {
    ProductCard(
        product = ProductModelEntity(
            "11",
            convertFactor1 = 1,
            convertFactor2 = 12,
            fullNameKala1 = "biscuit (1)",
            fullNameKala2 = "biscuit (2)",
            productCode = 659985,
            productGroupCode = 54544,
            productName = "biscuit",
            unit1 = "shelf",
            unit2 = "Carton",
            unitid1 = "54654",
            unitid2 = "4565",
            price = 98563,
            discountPercent = 98563,
            priceByDiscountPercent = 98563,
            productImage = ""
        ),
        onClick = {}
    )
}