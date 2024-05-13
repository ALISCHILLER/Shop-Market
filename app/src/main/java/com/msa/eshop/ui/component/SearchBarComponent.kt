@file:OptIn(ExperimentalMaterial3Api::class)

package com.msa.eshop.ui.component


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
@Preview
fun DockedSearch() {
    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }
    DockedSearchBar(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .semantics { traversalIndex = -1f },
        shape = RoundedCornerShape(7.dp),
        query = text,
        onQueryChange = {
            text = it
            // Call search function here with the current query
        },
        onSearch = { newQuery ->
        },
        active = false,
        onActiveChange = { active = it },
        placeholder = {
            Text(text = "جست و جوی محصول موردنظر")
        },
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
        },
        trailingIcon = {
            Row {
                IconButton(onClick = { /* open mic dialog */ }) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Mic"
                    )
                }
                if (active) {
                    IconButton(
                        onClick = { if (text.isNotEmpty()) text = "" else active = false }
                    ) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                    }
                }
            }
        }
    ) {

    }
}