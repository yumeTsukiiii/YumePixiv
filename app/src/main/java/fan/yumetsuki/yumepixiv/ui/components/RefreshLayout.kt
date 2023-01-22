package fan.yumetsuki.yumepixiv.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlin.math.*

object RefreshLayoutDefaults {

    @Composable
    fun flingScrollBehaviour(
        state: RefreshLayoutState = rememberRefreshLayoutState(),
        isReachTop: () -> Boolean = { false },
        isReachBottom: () -> Boolean = { false },
        fling: FlingBehavior = ScrollableDefaults.flingBehavior()
    ): RefreshLayoutScrollBehaviour {
        return FlingScrollBehaviour(
            state,
            isReachTop,
            isReachBottom,
            fling
        )
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RefreshLayout(
    modifier: Modifier = Modifier,
    header: (@Composable BoxScope.() -> Unit)? = null,
    footer: (@Composable BoxScope.() -> Unit)? = null,
    scrollBehaviour: RefreshLayoutScrollBehaviour,
    overscrollEffect: OverscrollEffect = ScrollableDefaults.overscrollEffect(),
    content: @Composable () -> Unit
) {
    val hasFooter = footer != null
    val hasHeader = header != null

    var footerHeight by scrollBehaviour.state.footerHeight

    var headerHeight by scrollBehaviour.state.headerHeight

    var contentOffset by scrollBehaviour.state.contentOffset

    LaunchedEffect(hasFooter, hasHeader, scrollBehaviour.state.showHeader, scrollBehaviour.state.showFooter, contentOffset) {
        if ((!hasFooter || !scrollBehaviour.state.showFooter) && contentOffset < 0) {
            contentOffset = 0
        } else if ((!hasHeader || !scrollBehaviour.state.showHeader) && contentOffset > 0) {
            contentOffset = 0
        }
    }

    Box(
        modifier = modifier
            .nestedScroll(scrollBehaviour.nestedScrollConnection)
            .overscroll(overscrollEffect)
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
    showFooter: Boolean = true,
    showHeader: Boolean = true
): RefreshLayoutState {
    val contentOffsetState = remember {
        mutableStateOf(0)
    }

    val headerHeightState = remember {
        mutableStateOf(0)
    }

    val footerHeightState = remember {
        mutableStateOf(0)
    }

    return remember(contentOffsetState, showHeader, showFooter) {
        DefaultRefreshLayoutState(
            contentOffsetState,
            headerHeightState,
            footerHeightState,
            showFooter,
            showHeader
        )
    }
}

interface RefreshLayoutState {

    val contentOffset: MutableState<Int>

    var headerHeight: MutableState<Int>

    var footerHeight: MutableState<Int>

    val showFooter: Boolean

    val showHeader: Boolean

}

class DefaultRefreshLayoutState(
    override val contentOffset: MutableState<Int>,
    override var headerHeight: MutableState<Int>,
    override var footerHeight: MutableState<Int>,
    override val showFooter: Boolean,
    override val showHeader: Boolean,
): RefreshLayoutState

interface RefreshLayoutScrollBehaviour {

    val state: RefreshLayoutState

    val nestedScrollConnection: NestedScrollConnection

}

class FlingScrollBehaviour(
    override val state: RefreshLayoutState,
    val isReachTop: () -> Boolean,
    val isReachBottom: () -> Boolean,
    val fling: FlingBehavior
): RefreshLayoutScrollBehaviour, NestedScrollConnection {

    override val nestedScrollConnection: NestedScrollConnection
        get() = this

    var contentOffset by state.contentOffset
    var headerHeight by state.headerHeight
    var footerHeight by state.footerHeight

    var lastContentScrollScope: ScrollScope? = null

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        if (source == NestedScrollSource.Drag && contentOffset != 0) {
            val consumed = scrollBy(available.y)
            if (consumed != null) {
                return available.copy(y = consumed)
            }
        }
        return super.onPreScroll(available, source)
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        if (source == NestedScrollSource.Drag) {
            val consumedOffset = scrollBy(available.y)
            if (consumedOffset != null) {
                return available.copy(y = consumedOffset)
            }
        }
        return super.onPostScroll(consumed, available, source)
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        if (contentOffset != 0) {
            return available.copy(y = available.y - performFling(available.y))
        }
        return super.onPreFling(available)
    }

    override suspend fun onPostFling(
        consumed: Velocity,
        available: Velocity
    ): Velocity {
        if (available.y != 0f && contentOffset == 0) {
            return available.copy(y = available.y - performFling(initialVelocity = available.y))
        }
        return super.onPostFling(consumed, available)
    }

    internal fun scrollBy(delta: Float): Float? {
        if (isReachTop() && state.showHeader) {
            val newContentOffset = (contentOffset + delta)
                .coerceIn(0f, headerHeight.toFloat())
            val consumedOffset = newContentOffset - contentOffset
            contentOffset = newContentOffset.roundToInt()
            return consumedOffset
        }
        if (isReachBottom() && state.showFooter) {
            val newContentOffset = (contentOffset + delta)
                .coerceIn(-footerHeight.toFloat(), 0f)
            val consumedOffset = newContentOffset - contentOffset
            contentOffset = newContentOffset.roundToInt()
            return consumedOffset
        }
        return null
    }

    internal suspend fun performFling(initialVelocity: Float): Float {
        if (!state.showHeader && !state.showFooter) {
            return initialVelocity
        }
        lastContentScrollScope = object : ScrollScope {
            override fun scrollBy(pixels: Float): Float {
                if (lastContentScrollScope != this) {
                    return 0f
                }
                return this@FlingScrollBehaviour.scrollBy(pixels) ?: pixels
            }
        }
        return with(fling) {
            lastContentScrollScope!!.performFling(initialVelocity)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun PreviewRefreshLayout() {

    val parentScrollState = rememberScrollState()
    val childScrollState = rememberLazyListState()
    val nestedScrollableState = rememberNestedScrollableState(
        parentScrollState = parentScrollState,
        childScrollState = childScrollState
    ) {
        childScrollState.firstVisibleItemIndex == 0 && childScrollState.firstVisibleItemScrollOffset == 0
    }

    RefreshLayout(
        scrollBehaviour = RefreshLayoutDefaults.flingScrollBehaviour(
            isReachTop = {
                false
//          contentScrollableState.firstVisibleItemIndex == 0 && contentScrollableState.firstVisibleItemScrollOffset == 0
            }, isReachBottom = {
                childScrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == childScrollState.layoutInfo.totalItemsCount - 1
            }, state = rememberRefreshLayoutState()
        ),
        header = {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(256.dp)
                .background(Color.Red))
        }, footer = {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(256.dp)
                .background(Color.Red))
        }
    ) {

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier
                .nestedScrollable(
                    nestedScrollableState = nestedScrollableState,
                    orientation = Orientation.Vertical
                )
                .verticalScroll(parentScrollState, enabled = false)
                .fillMaxWidth()
                .wrapContentHeight(unbounded = true)) {

                Box(modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth()
                    .background(Color.Red))

                LazyColumn(
                    state = childScrollState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(this@BoxWithConstraints.maxHeight),
                    userScrollEnabled = false) {

                    items(30) {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)) {
                            Text(text = "2333", modifier = Modifier.align(Alignment.Center))
                        }
                    }

                }
            }
        }

    }

}