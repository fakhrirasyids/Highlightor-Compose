package com.fakhrirasyids.highlightor.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class HighlightorState private constructor(
    initialIndex: Int
) {
    val highlightorTargets = mutableStateMapOf<Int, HighlightorTarget>()
    val currentTarget: HighlightorTarget?
        get() = highlightorTargets[currentTargetIndex]

    private var _currentTargetIndex by mutableStateOf(initialIndex)
    var currentTargetIndex: Int
        get() = _currentTargetIndex
        set(value) {
            _currentTargetIndex = value
        }

    companion object {
        fun create(initialIndex: Int = 0): HighlightorState {
            return HighlightorState(initialIndex)
        }
    }
}