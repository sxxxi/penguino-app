package com.penguino.data.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FocusedList<T>(window: Int = 5): Collection<T> {
	override val size: Int
		get() {
			return _history.value.size
		}

	private var windowSize: Int = window
		set(value) {
			field = value
			updatePtr()
		}
	var ptr = -1
		private set

	private var _history = MutableStateFlow<List<T>>(listOf())
	val history: StateFlow<List<T>> = _history	// Let's convert this to a state once we start integrating with the ui.

	private fun updatePtr() {
		ptr = if (history.value.isEmpty())
			-1
		else if (history.value.size < windowSize)
			0
		else history.value.size - windowSize
	}

	/**
	 * Retrieve up to the size of `window` from the end of the list
	 */
	fun getChunk(): List<T> {
		updatePtr()	// Make sure ptr is fresh and crispy
		return if(ptr < 0) {
			listOf()
		} else {
			history.value.slice((ptr) until history.value.size)
		}
	}

	fun addEntry(entry: List<T>) {
		_history.value += entry
		updatePtr()
	}

	override fun contains(element: T): Boolean {
		return _history.value.contains(element)
	}

	override fun containsAll(elements: Collection<T>): Boolean {
		return _history.value.containsAll(elements)
	}

	override fun isEmpty(): Boolean {
		return _history.value.isEmpty()
	}

	override fun iterator(): Iterator<T> {
		return _history.value.iterator()
	}
}