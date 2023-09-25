package com.penguino.data.mappers.contracts

interface EDMapper<E, D> {
	fun entityToDomain(entity: E): D
}