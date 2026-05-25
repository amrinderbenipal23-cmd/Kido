package com.kido.app.feature.parent

import androidx.lifecycle.ViewModel

/**
 * Generates a single-digit multiplication problem the parent must solve to
 * pass the gate. Stays deliberately simple — its job is to deter a 4-year-old,
 * not a security boundary.
 */
class ParentGateViewModel : ViewModel() {

    val a: Int = (3..9).random()
    val b: Int = (3..9).random()
    private val expected: Int = a * b

    fun verify(answer: String): Boolean {
        val n = answer.trim().toIntOrNull() ?: return false
        return n == expected
    }
}
