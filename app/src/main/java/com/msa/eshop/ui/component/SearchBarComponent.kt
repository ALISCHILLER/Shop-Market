@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)

package com.msa.eshop.ui.component


import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.msa.eshop.R
import com.msa.eshop.ui.theme.*


@Composable
@Preview
fun DockedSearchPreview(){
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        DockedSearch(
            {}
        )
    }
}
@Composable
fun DockedSearch(
    onQueryChange: (String) -> Unit,
) {
    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }
    DockedSearchBar(
        colors = SearchBarDefaults.colors(
            containerColor = Color.White
        ),
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 8.dp)
            .fillMaxWidth()
            .border(width = 1.dp, color = barcolor, shape = RoundedCornerShape(10.dp))
            .semantics { traversalIndex = -1f },
        shape =RoundedCornerShape(10.dp) ,
        query = text,
        onQueryChange = {
            text = it
            onQueryChange(it)
            // Call search function here with the current query
        },
        onSearch = { newQuery ->
        },
        active = false,
        onActiveChange = { active = it },
        placeholder = {
            Text(
                text = stringResource(id = R.string.title_search),
                color = barcolorlow
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Search",
                tint = barcolorlow
            )
        },
        trailingIcon = {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                VerticalDivider(modifier = Modifier.padding(vertical = 7.dp),
                    color = barcolor, thickness = 2.dp)
                IconButton(onClick = { /* open mic dialog */ }) {
                    Icon(
                        modifier = Modifier.size(25.dp),
                        painter = painterResource(id = R.drawable.ic_microphone),
                        contentDescription = "Mic",
                        tint = barcolorlow
                    )
                }
                if (active) {
                    IconButton(
                        onClick = { if (text.isNotEmpty()) text = "" else active = false }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = barcolorlow
                        )
                    }
                }
            }
        }
    ) {

    }
}