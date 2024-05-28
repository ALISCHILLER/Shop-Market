package com.msa.eshop.ui.component.graidC

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.min

@Composable
fun NonlazyGrid(
    columns: Int,
    itemCount: Int,
    modifier: Modifier = Modifier,
    content: @Composable() (Int) -> Unit
) {
    Column(modifier = modifier) {
        var rows = (itemCount / columns)
        if (itemCount.mod(columns) > 0) {
            rows += 1
        }

        for (rowId in 0 until rows) {
            val firstIndex = rowId * columns

            Row {
                for (columnId in 0 until columns) {
                    val index = firstIndex + columnId
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        if (index < itemCount) {
                            content(index)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NonlazyGrids(
    columns: Int,
    itemCount: Int,
    content: @Composable (Int) -> Unit
) {
    val rows = (itemCount + columns - 1) / columns // محاسبه تعداد ردیف‌ها

    Column {
        repeat(rows) { rowIndex ->
            Row(Modifier.fillMaxWidth()) {
                val startIndex = rowIndex * columns
                val endIndex = min(startIndex + columns, itemCount)
                for (i in startIndex until endIndex) {
                    Box(Modifier.weight(1f)) {
                        content(i) // فراخوانی تابع content برای نمایش هر آیتم
                    }
                }
                // اگر آیتم‌های کمتر از تعداد ستون‌ها باشد، یک Spacer اضافه می‌شود
                if (endIndex - startIndex < columns) {
                    Spacer(
                        modifier = Modifier
                            .weight((columns - (endIndex - startIndex)).toFloat())
                    )
                }
            }
        }
    }
}