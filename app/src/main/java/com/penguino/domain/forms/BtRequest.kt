package com.penguino.domain.forms

data class BtRequest(
    private val mapping: String,
    val argument: String
) {
    override fun toString(): String {
        return "<mapping>$mapping<mapping><arg>$argument<arg>"
    }
}
