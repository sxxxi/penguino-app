package com.penguino.prestentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun <T> ListContainer(
	modifier: Modifier = Modifier,
	cornerRadius: Dp = 15.dp,
	listBackgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
	minToMaxHeight: Pair<Dp, Dp> = 100.dp to 300.dp,
	itemList: List<T>,
	listItem: @Composable LazyItemScope.(T) -> Unit,
) {
	val (hMin, hMax) = remember { minToMaxHeight }
	Box(
		modifier = modifier
			.clip(RoundedCornerShape(cornerRadius))
	) {
		LazyColumn(
			modifier = Modifier
				.shadow(4.dp)
				.fillMaxWidth()
				.background(listBackgroundColor)
				.heightIn(min = hMin, max = hMax),
		) {
			items(items = itemList, key = { it.hashCode() }) { item ->
				listItem(item)
			}
		}
	}
}

@Composable
fun <T> ListComponent(
	listItems: List<T>,
	onListEmpty: @Composable () -> Unit,
	content: @Composable LazyItemScope.(T) -> Unit
) {
	AnimatedContent(targetState = listItems, label = "") { items ->
		if (items.isNotEmpty()) {
			ListContainer(
				itemList = items
			) { info ->
				content(info)
			}
		} else {
			onListEmpty()
		}
	}
}

@Composable
fun ListComponentHeader(
	text: String,
	actions: @Composable RowScope.() -> Unit = {}
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(
			text = text,
			style = MaterialTheme.typography.displayMedium +
					TextStyle(fontWeight = FontWeight.Bold)
		)
		actions()
	}
}
