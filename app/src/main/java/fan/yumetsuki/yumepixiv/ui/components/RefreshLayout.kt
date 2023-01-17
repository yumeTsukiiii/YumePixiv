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
import fan.yumetsuki.yumepixiv.ui.foundation.detectVerticalDragGestures
import fan.yumetsuki.yumepixiv.ui.foundation.detectVerticalDragGesturesIgnoreConsumed
import kotlinx.coroutines.launch
import kotlin.math.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RefreshLayout(
    isReachTop: () -> Boolean,
    isReachBottom: () -> Boolean,
    state: RefreshLayoutState,
    modifier: Modifier = Modifier,
    header: (@Composable BoxScope.() -> Unit)? = null,
    footer: (@Composable BoxScope.() -> Unit)? = null,
    flingBehavior: FlingBehavior? = null,
    // TODO 支持 overscroll
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

    var contentOffset by state.contentOffset as MutableState<Int>

    LaunchedEffect(hasFooter, hasHeader, contentOffset) {
        if (!hasFooter && contentOffset < 0) {
            contentOffset = 0
        } else if (!hasHeader && contentOffset > 0) {
            contentOffset = 0
        }
    }

    val fling = flingBehavior ?: ScrollableDefaults.flingBehavior()
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .nestedScroll(object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    println("TestDelta: RefreshLayout preScroll $available")
                    if (contentOffset != 0) {
                        println("TestRefreshLayout: intercept ${available}; isReachBottom: ${isReachBottom()}")
                        return available
                    }
                    return super.onPreScroll(available, source)
                }

                override suspend fun onPreFling(available: Velocity): Velocity {
                    val scope = object : ScrollScope {
                        override fun scrollBy(pixels: Float): Float {
                            if (isReachTop()) {
                                val newContentOffset = (contentOffset + pixels)
                                    .coerceIn(0f, headerHeight.toFloat())
                                val consumed = newContentOffset - contentOffset
                                contentOffset = newContentOffset.roundToInt()
                                return consumed
                            }
                            if (isReachBottom()) {
                                val newContentOffset = (contentOffset + pixels)
                                    .coerceIn(-footerHeight.toFloat(), 0f)
                                val consumed = newContentOffset - contentOffset
                                contentOffset = newContentOffset.roundToInt()
                                return consumed
                            }
                            return pixels
                        }
                    }
                    with(fling) {
                        coroutineScope.launch {
                            scope.performFling(available.y)
                        }
                    }
                    return super.onPreFling(available)
                }
            })
            .pointerInput(hasFooter, hasHeader, isReachTop, isReachBottom) {
                if (!hasFooter && !hasHeader) {
                    return@pointerInput
                }
                detectVerticalDragGesturesIgnoreConsumed(
//                    canDetectDrag = {
//                        isReachTop() || isReachBottom()
//                    }
                ) { _, delta ->
                    println("TestDelta: delta")
                    if (isReachTop() && hasHeader) {
                        contentOffset = (contentOffset + delta)
                            .roundToInt()
                            .coerceIn(0, headerHeight)
                    } else if (isReachBottom() && hasFooter) {
                        contentOffset = (contentOffset + delta)
                            .roundToInt()
                            .coerceIn(-footerHeight, 0)
                    }
                }
            }
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
    contentScrollableState: ScrollableState? = null
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

    val contentOffset: State<Int>

    suspend fun closeFooter(scrollContent: Boolean = false)

}

class DefaultRefreshLayoutState(
    private val contentOffsetState: MutableState<Int>,
    private val contentScrollableState: ScrollableState? = null
): RefreshLayoutState {

    override val contentOffset: State<Int>
        get() = contentOffsetState

    override suspend fun closeFooter(scrollContent: Boolean) {
        if (contentOffsetState.value < 0) {
            if (scrollContent) {
                contentScrollableState?.scrollBy(contentOffsetState.value.toFloat())
            }
            contentOffsetState.value = 0
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun PreviewRefreshLayout() {

    val contentScrollableState = rememberLazyListState()

    RefreshLayout(isReachTop = {
                               false
//        contentScrollableState.firstVisibleItemIndex == 0 && contentScrollableState.firstVisibleItemScrollOffset == 0
    }, isReachBottom = {
          contentScrollableState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == contentScrollableState.layoutInfo.totalItemsCount - 1
    }, state = rememberRefreshLayoutState(
        contentScrollableState = contentScrollableState
    ), header = {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(256.dp)
            .background(Color.Red))
    }, footer = {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(256.dp)
            .background(Color.Red))
    }) {

//        Column(modifier = Modifier.fillMaxSize()) {
//
//        }

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier
                .nestedVerticalScroll(
                    state = rememberScrollState(),
                    isChildReachTop = {
                        contentScrollableState.firstVisibleItemIndex == 0 && contentScrollableState.firstVisibleItemScrollOffset == 0
                    })
                .fillMaxWidth()
                .wrapContentHeight(unbounded = true)) {

                Box(modifier = Modifier.height(64.dp).fillMaxWidth().background(Color.Red))

                LazyColumn(modifier = Modifier
                    .fillMaxWidth()
                    .height(this@BoxWithConstraints.maxHeight), state = contentScrollableState) {

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

@Preview
@Composable
fun PreviewDragGesture() {

    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectVerticalDragGestures(
                requireUnconsumed = false,
                onVerticalDrag = { _, delta ->
                    println("GestureDetect: $delta")
                }
            )
        }
    ) {

        LazyColumn(modifier = Modifier.fillMaxSize()) {

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