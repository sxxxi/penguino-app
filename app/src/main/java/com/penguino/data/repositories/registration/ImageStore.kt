package com.penguino.data.repositories.registration

import com.penguino.domain.models.Image

interface ImageStore {
	fun getImage(path: String): Image?
	fun saveImage(image: Image)
	fun deleteImage(path: String)
}