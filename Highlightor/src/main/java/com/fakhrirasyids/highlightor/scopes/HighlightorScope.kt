package com.fakhrirasyids.highlightor.scopes

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fakhrirasyids.highlightor.model.HighlightorState
import com.fakhrirasyids.highlightor.model.HighlightorTarget
import kotlin.math.roundToInt

class HighlightorScope(
    private val state: HighlightorState,
    private val onHighlightorComplete: () -> Unit
) {
    fun Modifier.highlightor(index: Int, description: String? = null) =
        highlightorTarget(
            state = state,
            index = index,
            content = {
                HighlightorText(
                    description = description,
                    index = index,
                    indexSize = state.highlightorTargets.size,
                    onFinishClick = { handleFinishClick() },
                    onNextClick = { handleNextClick() })
            }
        )

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    internal fun HighlightorBox(state: HighlightorState, constraints: Constraints) {
        val currentTarget = state.currentTarget

        AnimatedContent(
            modifier = Modifier.fillMaxSize(),
            targetState = currentTarget,
            transitionSpec = {
                fadeIn(animationSpec = tween(500)) with fadeOut(animationSpec = tween(500))
            }
        ) { currentState ->
            currentState?.let {
                HighlightorOverlay(content = it)
            }
        }

        currentTarget?.let { highlightorContent ->
            val composeWidth =
                remember(highlightorContent) { highlightorContent.coordinates.size.width }
            val composeHeight =
                remember(highlightorContent) { highlightorContent.coordinates.size.height }
            val xContent =
                remember(highlightorContent) { highlightorContent.coordinates.positionInRoot().x.toInt() }
            val yContent =
                remember(highlightorContent) { highlightorContent.coordinates.positionInRoot().y.toInt() }

            var highlightorSize by remember { mutableStateOf(IntSize.Zero) }

            val xDisplacement by remember(xContent, composeWidth, highlightorSize) {
                derivedStateOf {
                    val displacedX =
                        xContent + (xContent + composeWidth / 2) - (xContent + highlightorSize.width / 2)
                    when {
                        displacedX < 0 -> 0
                        displacedX + highlightorSize.width > constraints.maxWidth -> displacedX
                        else -> displacedX
                    }
                }
            }

            val outOfBoundsStart = remember(xDisplacement) { xDisplacement < 0 }
            val outOfBoundsEnd =
                remember(xDisplacement) { xDisplacement + highlightorSize.width > constraints.maxWidth }
            val outOfBoundsTop = remember(xContent) { yContent < 0 }
            val outOfBoundsBottom =
                remember(xContent) { yContent + highlightorSize.height > constraints.maxHeight }

            val drawX by remember(xDisplacement, highlightorSize, constraints) {
                derivedStateOf {
                    when {
                        outOfBoundsStart -> 0
                        outOfBoundsEnd -> xDisplacement - (xDisplacement + highlightorSize.width - constraints.maxWidth)
                        else -> xDisplacement
                    }
                }
            }

            val drawY by remember(yContent, highlightorSize, constraints) {
                derivedStateOf {
                    val ySafeBottom =
                        yContent - ((yContent + highlightorSize.height) - constraints.maxHeight)

                    var safeY = when {
                        outOfBoundsTop -> 0
                        outOfBoundsBottom -> ySafeBottom
                        else -> yContent
                    }

                    if ((safeY in yContent..(yContent + composeHeight)) || safeY !in yContent..(yContent + composeHeight)) {
                        val adjustment = when {
                            safeY + composeHeight + highlightorSize.height < constraints.maxHeight -> composeHeight + 32
                            safeY - composeHeight - highlightorSize.height > 0 -> -highlightorSize.height - 32
                            else -> 0
                        }
                        safeY += adjustment
                    }

                    safeY
                }
            }

            val xAnim = remember { Animatable(0f) }
            val yAnim = remember { Animatable(0f) }
            var visible by remember(highlightorContent.index) { mutableStateOf(false) }

            LaunchedEffect(drawX, drawY) {
                xAnim.animateTo(drawX.toFloat(), tween(50))
                yAnim.animateTo(drawY.toFloat(), tween(50))
                visible = true
            }

            AnimatedVisibility(
                modifier = Modifier
                    .onSizeChanged { highlightorSize = it }
                    .offset {
                        IntOffset(
                            xAnim.value.roundToInt(),
                            yAnim.value.roundToInt()
                        )
                    },
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = 300,
                        delayMillis = 200,
                        easing = FastOutLinearInEasing
                    )
                ),
                exit = fadeOut(
                    animationSpec = tween(durationMillis = 150)
                ),
                visible = visible
            ) {
                if (visible) {
                    Box {
                        highlightorContent.content.invoke(this)
                    }
                }
            }
        }
    }

    @Composable
    private fun HighlightorOverlay(content: HighlightorTarget) {
        Canvas(modifier = Modifier.fillMaxSize(), onDraw = {
            val cornerRadius = 12f
            val focusPadding = 8
            val offSetInRoot = content.coordinates.positionInRoot()
            val contentSize = content.coordinates.size

            val pathToClip = Path().apply {
                addRoundRect(
                    RoundRect(
                        left = offSetInRoot.x - focusPadding,
                        top = offSetInRoot.y - focusPadding,
                        right = offSetInRoot.x + contentSize.width.toFloat() + focusPadding,
                        bottom = offSetInRoot.y + contentSize.height.toFloat() + focusPadding,
                        radiusX = cornerRadius,
                        radiusY = cornerRadius
                    )
                )
            }

            clipPath(pathToClip, clipOp = ClipOp.Difference) {
                drawRect(
                    SolidColor(
                        value = Color.Black.copy(alpha = 0.5f)
                    ),
                    topLeft = Offset(0f, 0f)
                )
            }
        })
    }

    @Composable
    private fun HighlightorText(
        description: String?,
        index: Int,
        indexSize: Int,
        onNextClick: (() -> Unit),
        onFinishClick: (() -> Unit)
    ) {
        Column(
            modifier = Modifier
                .animateContentSize()
                .padding(horizontal = 12.dp)
                .background(color = Color.White, shape = RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            description?.let {
                Text(
                    text = it,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.align(Alignment.End)
            ) {
                if (index == indexSize - 1) {
                    TextButton(onClick = onFinishClick) {
                        Text(
                            text = "Finish",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            style = TextStyle(color = Color.DarkGray)
                        )
                    }
                } else {
                    TextButton(onClick = onFinishClick) {
                        Text(
                            text = "Skip",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            style = TextStyle(color = Color.DarkGray)
                        )
                    }
                    TextButton(onClick = onNextClick) {
                        Text(
                            text = "Next",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            style = TextStyle(color = Color.DarkGray)
                        )
                    }
                }
            }
        }
    }

    private fun Modifier.highlightorTarget(
        state: HighlightorState,
        index: Int,
        content: (@Composable BoxScope.() -> Unit)
    ): Modifier = onGloballyPositioned { coordinates ->
        state.highlightorTargets[index] = HighlightorTarget(
            index = index,
            coordinates = coordinates,
            content = content
        )
    }

    private fun handleFinishClick() {
        state.currentTargetIndex = state.highlightorTargets.size + 1
        if (state.currentTargetIndex >= state.highlightorTargets.size) {
            onHighlightorComplete()
        }
    }

    private fun handleNextClick() {
        state.currentTargetIndex++
    }
}