package com.penguino.data.utils.mappers.contracts

interface EDMapper<E, D> {
	fun entityToDomain(entity: E): D
}