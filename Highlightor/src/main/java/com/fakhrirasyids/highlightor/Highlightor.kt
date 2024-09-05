package com.fakhrirasyids.highlightor

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.fakhrirasyids.highlightor.model.HighlightorState
import com.fakhrirasyids.highlightor.scopes.HighlightorScope
import com.fakhrirasyids.highlightor.utils.rememberHighlightorState

@Composable
fun Highlightor(
    modifier: Modifier = Modifier,
    showHighlightor: Boolean,
    onCompleted: () -> Unit,
    state: HighlightorState = rememberHighlightorState(),
    content: @Composable HighlightorScope.() -> Unit
) {
    val scope = remember(state, showHighlightor) {
        HighlightorScope(state, onCompleted)
    }

    BoxWithConstraints(modifier = modifier) {
        scope.content()

        if (showHighlightor) {
            scope.HighlightorBox(
                state = state,
                constraints = this.constraints
            )
        }
    }
}