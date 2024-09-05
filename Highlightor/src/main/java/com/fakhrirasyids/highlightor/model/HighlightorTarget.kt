package com.fakhrirasyids.highlightor.model

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.LayoutCoordinates

data class HighlightorTarget(
    val index: Int,
    val coordinates: LayoutCoordinates,
    val content: (@Composable BoxScope.() -> Unit)
)