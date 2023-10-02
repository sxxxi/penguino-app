package com.penguino.prestentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlin.math.ceil

@Composable
fun VerticalGrid(columns: Int, items: List<@Composable () -> Unit>) {
    LazyColumn {
        var current = 0
        val rows = ceil(items.size / columns.toFloat()).toInt()
        items(count = rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(columns) {
                    if (current < items.size)
                        items[current++]()
                }

            }
        }
    }

}