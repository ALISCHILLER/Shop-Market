package com.msa.eshop.ui.common.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.msa.eshop.R
import com.msa.eshop.data.local.entity.ProductGroupEntity
import com.msa.eshop.ui.screen.home.HomeViewModel

@Composable
fun ProductGroupCard(
    productGroupEntity: ProductGroupEntity,
    onClick: (ProductGroupEntity) -> Unit,
    isSelected: Boolean,
    viewModel: HomeViewModel = hiltViewModel()
) {


    var isSelectedState by remember { mutableStateOf(isSelected) }

    val iconTint = if (isSelected) Color.White else Color.Red
    val textBackground = if (isSelected) Color.Red else Color.White
    val iconUrl = if (!isSelected)
         productGroupEntity.productCategoryImage
    else
        productGroupEntity.productCategoryImageUnselect


    Row(
        modifier = Modifier
            .padding(10.dp)
            .width(80.dp)
            .height(80.dp)
            .clickable {
                onClick(productGroupEntity)
                viewModel.getProduct(productGroupEntity)
            }
    ) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .weight(1f)
                .padding(1.dp),
            color = textBackground
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                iconUrl?.let {
                    ProductGroupIcon(
                        iconUrl = it,
                        tint = iconTint
                    )
                }
                productGroupEntity.productCategoryName?.let {
                    Text(
                        text = it,
                        color = iconTint
                    )
                }

            }
        }
    }
}

@Composable
fun ProductGroupIcon(iconUrl:String, tint: Color) {

    AsyncImage(
        model = iconUrl,
        contentDescription = "Icon Product Group",
        modifier = Modifier
            .size(50.dp, 50.dp),
        error = painterResource(id = R.drawable.not_load_image)
    )
}

@Preview
@Composable
private fun ItemProductGroupScreenPreview() {
    ProductGroupCard(
        ProductGroupEntity(
            1,
            "آرد",
            "",
            ""
        ),
        onClick = { /* Do something on click */ },
        false
    )
}