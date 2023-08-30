package com.penguino.data.utils

import org.junit.jupiter.api.Test

class FocusedListTest {
	@Test
	fun emptyListPtrIsNegative() {
		val focusedList = FocusedList<Int>()
		assert(focusedList.ptr < 0) {
			"Pointer when list is empty not negative."
		}
	}

	@Test
	fun listSizeLessThanWindowSizePtrZero() {
		val windowSize = 5
		val focusedList = FocusedList<Int>(windowSize)
		val emptyList = focusedList.ptr == -1 && windowSize > focusedList.size

		focusedList.addEntry(listOf(1, 2, 3, 4))
		val listLtWindow = focusedList.ptr == 0 && windowSize > focusedList.size

		focusedList.addEntry(listOf(5))
		val listEqWindow = focusedList.ptr == 0 && windowSize == focusedList.size

		focusedList.addEntry(listOf(6))
		val listGtWindow = focusedList.ptr == 1 && windowSize < focusedList.size

		assert(emptyList && listLtWindow && listEqWindow && listGtWindow) {
			"Pointer value when list size is smaller <= window size not 0"
		}
	}

	/**
	 * Collection size must be updated properly when adding entry/entries
	 */
	@Test
	fun addingEntriesUpdatesEverythingProperly() {
		val focusedList = FocusedList<Int>(3)
		val initiallyEmpty = focusedList.isEmpty()

		focusedList.addEntry(listOf(1, 2, 3))
		val newSizeCorrect = focusedList.size == 3

		assert(initiallyEmpty && newSizeCorrect) {
			"Something wrong adding entry to the list"
		}
	}

	/**
	 * Test the sizes of `getChunk()`.
	 * - When list size is zero, return an empty list.
	 * - When list size is less than the window size, returned chunk size must be less than window size.
	 * - When list size is greater than or equals to window, chunk size must be equals to window size.
	 */
	@Test
	fun chunkRetrievalTest() {
		val windowSize = 3
		val focusedList = FocusedList<Int>(windowSize)

		val emptyChunk = focusedList.getChunk().isEmpty()

		focusedList.addEntry(listOf(1, 2))
		val listLtWindowChunk = focusedList.getChunk().size == 2

		focusedList.addEntry(listOf(3, 4))
		val listGtWindowChunk = focusedList.getChunk().size == windowSize

		assert(emptyChunk && listLtWindowChunk && listGtWindowChunk) {
			"Window size inconsistent."
		}
	}
}