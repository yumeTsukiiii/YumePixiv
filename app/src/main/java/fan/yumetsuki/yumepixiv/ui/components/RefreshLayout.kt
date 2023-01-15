package fan.yumetsuki.yumepixiv.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.overscroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RefreshLayout(
    isReachTop: (delta: Float) -> Boolean,
    isReachBottom: (delta: Float) -> Boolean,
    state: RefreshLayoutState,
    modifier: Modifier = Modifier,
    header: (@Composable BoxScope.() -> Unit)? = null,
    footer: (@Composable BoxScope.() -> Unit)? = null,
    overscrollEffect: OverscrollEffect = ScrollableDefaults.overscrollEffect(),
    content: @Composable () -> Unit
) {
    val hasFooter = footer != null
    val hasHeader = header != null

    var footerHeight by remember {
        mutableStateOf(0)
    }

    var headerHeight by remember {
        mutableStateOf(0)
    }

    val contentScrollableState = state.contentScrollableState
    var contentOffset by state.contentOffset as MutableState<Int>

    LaunchedEffect(hasFooter, hasHeader, contentOffset) {
        if (!hasFooter && contentOffset < 0) {
            contentOffset = 0
        } else if (!hasHeader && contentOffset > 0) {
            contentOffset = 0
        }
    }

    Box(
        modifier = modifier.scrollable(
            orientation = Orientation.Vertical,
            overscrollEffect = overscrollEffect,
            state = rememberScrollableState { delta ->
                val consumed = contentScrollableState.dispatchRawDelta(delta)
                val remain = delta - consumed
                // content 滚动容器全部消费，还可以滚动，需要继续分发
                if (remain == 0f && contentOffset == 0) {
                    return@rememberScrollableState delta
                }
                if (!(hasFooter || hasHeader)) {
                    return@rememberScrollableState 0f
                }
                if (delta < 0) {
                    if (contentOffset > 0) {
                        contentOffset = max(0, (contentOffset + delta).roundToInt())
                        return@rememberScrollableState delta
                    }
                    if (!hasFooter || contentOffset == -footerHeight) {
                        return@rememberScrollableState 0f
                    }
                    if (contentOffset < 0 || isReachBottom(delta)) {
                        contentOffset =
                            max(-footerHeight, (contentOffset + delta).roundToInt())
                    }
                    return@rememberScrollableState if (contentOffset == -footerHeight) {
                        0f
                    } else {
                        delta
                    }
                } else {
                    if (contentOffset < 0) {
                        contentOffset = min(0, (contentOffset + delta).roundToInt())
                        return@rememberScrollableState delta
                    }
                    if (!hasHeader || contentOffset == headerHeight) {
                        return@rememberScrollableState 0f
                    }
                    if (contentOffset > 0 || isReachTop(delta)) {
                        contentOffset =
                            min(headerHeight, (contentOffset + delta).roundToInt())
                    }
                    return@rememberScrollableState delta
                }
            }
        ).overscroll(overscrollEffect)
            .onSizeChanged {
                overscrollEffect.isEnabled = it.height != 0
            }
    ) {
        if (header != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .offset {
                        IntOffset(0, contentOffset - headerHeight)
                    }
                    .onSizeChanged {
                        headerHeight = it.height
                    }
            ) {
                header()
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset {
                    IntOffset(0, contentOffset)
                }
        ) {
            content()
        }
        if (footer != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .offset {
                        IntOffset(0, contentOffset + footerHeight)
                    }
                    .onSizeChanged {
                        footerHeight = it.height
                    }
            ) {
                footer()
            }
        }
    }

}

@Composable
fun rememberRefreshLayoutState(
    contentScrollableState: ScrollableState
): RefreshLayoutState {
    val contentOffsetState = remember {
        mutableStateOf(0)
    }

    return remember(contentScrollableState, contentOffsetState) {
        DefaultRefreshLayoutState(
            contentOffsetState,
            contentScrollableState
        )
    }
}

interface RefreshLayoutState {

    val contentScrollableState: ScrollableState

    val contentOffset: State<Int>

    suspend fun closeFooter(scrollContent: Boolean = false)

}

class DefaultRefreshLayoutState(
    private val contentOffsetState: MutableState<Int>,
    override val contentScrollableState: ScrollableState
): RefreshLayoutState {

    override val contentOffset: State<Int>
        get() = contentOffsetState

    override suspend fun closeFooter(scrollContent: Boolean) {
        if (contentOffsetState.value < 0) {
            if (scrollContent) {
                contentScrollableState.scrollBy(contentOffsetState.value.toFloat())
            }
            contentOffsetState.value = 0
        }
    }

}