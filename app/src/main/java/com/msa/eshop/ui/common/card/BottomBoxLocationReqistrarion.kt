package com.msa.eshop.ui.common.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.msa.eshop.R
import com.msa.eshop.ui.theme.Typography
import com.msa.eshop.ui.theme.barcolorlight2

@Composable
fun BottomBoxLocationReqistrarion(
    modifier: Modifier = Modifier,
    onClickNavigateToAddress : () -> Unit,

) {
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
                    },
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        stringResource(id = R.string.address_registration),
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
                        onClickNavigateToAddress()

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