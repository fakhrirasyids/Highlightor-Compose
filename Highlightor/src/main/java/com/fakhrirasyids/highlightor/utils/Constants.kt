package com.fakhrirasyids.highlightor.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.fakhrirasyids.highlightor.model.HighlightorState

@Composable
fun rememberHighlightorState(
    initialIndex: Int = 0,
) = remember {
    HighlightorState.create(
        initialIndex = initialIndex
    )
}
