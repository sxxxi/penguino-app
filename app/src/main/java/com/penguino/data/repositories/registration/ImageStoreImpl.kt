package com.penguino.data.repositories.registration

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.penguino.data.models.Image
import java.io.File
import javax.inject.Inject

class ImageStoreImpl @Inject constructor() : ImageStore {
	override fun getImage(path: String): Image? {
		File(path).let { pfp ->
			return if (pfp.exists()) {
				Image(
					bitmap = BitmapFactory.decodeFile(pfp.path),
					filePath = path
				)
			} else null
		}
	}

	override fun saveImage(image: Image) {
		File(image.filePath)
			.apply { createNewFile() }
			.outputStream().use { os ->
				image.bitmap.compress(Bitmap.CompressFormat.JPEG, 50, os)
			}
	}

	override fun deleteImage(path: String) {
		File(path).let { existing ->
			if (existing.exists()) existing.delete()
		}
	}
}