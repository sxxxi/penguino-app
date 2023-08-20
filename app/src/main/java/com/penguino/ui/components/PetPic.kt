package com.penguino.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.penguino.R
import com.penguino.data.models.Image

@Composable
fun PetPic(
	modifier: Modifier = Modifier,
	contentScale: ContentScale = ContentScale.FillWidth,
	painter: Painter = painterResource(id = R.drawable.pet_img_default),
	containerPadding: PaddingValues = PaddingValues(0.dp),
	image: Image? = null
) {
	Box(Modifier.padding(containerPadding)) {
		image?.let { img ->
			Image(
				modifier = Modifier
					.clip(RoundedCornerShape(100))
					.then(modifier),
				bitmap = img.bitmap.asImageBitmap(),
				contentDescription = "Pet image",
				contentScale = contentScale
			)
		} ?: Image(
			modifier = Modifier
				.clip(RoundedCornerShape(100))
				.then(modifier),
			painter = painter,
			contentDescription = "Pet image",
			contentScale = contentScale
		)
	}
}