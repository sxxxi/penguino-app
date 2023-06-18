package com.penguino.data.local.models

interface ToModel<T> {
	fun toModel(): T
}