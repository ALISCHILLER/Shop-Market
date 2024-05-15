package com.msa.eshop.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.msa.eshop.ui.theme.Typography
@Composable
fun TitleGrouping(
    modifier: Modifier = Modifier,
    titleText:String,
) {

    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = titleText,
            style = Typography.titleLarge
        )

        Row {
            Text(
                text = "مشاهده همه",
                style = Typography.titleSmall
            )

            Image(imageVector = Icons.Default.ArrowBackIosNew, contentDescription ="" )
        }
    }
}


@Preview
@Composable
private fun TitleGroupingPreview() {

    TitleGrouping(titleText = "خرید براساس دسته بندی")
}