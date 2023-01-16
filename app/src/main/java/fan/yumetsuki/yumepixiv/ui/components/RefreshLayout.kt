package fan.yumetsuki.yumepixiv.ui.components

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
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
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RefreshLayout(
    isReachTop: () -> Boolean,
    isReachBottom: () -> Boolean,
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

    val fling = ScrollableDefaults.flingBehavior()
    val coroutineScope = rememberCoroutineScope()

    suspend fun AwaitPointerEventScope.awaitFirstDownOnPass(
        pass: PointerEventPass,
        requireUnconsumed: Boolean
    ): PointerInputChange {
        var event: PointerEvent
        do {
            event = awaitPointerEvent(pass)
        } while (
            !event.changes.all {
                if (requireUnconsumed) it.changedToDown() else it.changedToDownIgnoreConsumed()
            }
        )
        return event.changes[0]
    }

    Box(
        modifier = modifier
            .pointerInput(hasFooter, hasHeader) {
                awaitPointerEventScope {
                    val down = awaitFirstDownOnPass(pass = PointerEventPass.Initial, requireUnconsumed = true)
                    var change =
                        awaitVerticalTouchSlopOrCancellation(down.id) { change, over ->
                            println("SelfDragUp: ${change.positionChange().y}")
                        }
                    while (change != null && change.pressed) {
                        // TODO 扒出来原来，删掉 isConsumed 的判断，这里即使子节点消费了也需要监听，
                        //   相当于 dispatchTouchEvent 的流程，但并不拦截，反而是 onTouchEvent 的 up 流程，但事件自身不能拦截消费了
                        change = awaitVerticalDragOrCancellation(change.id)
                        if (change != null && change.pressed) {
                            println("SelfDrag: ${change.positionChange().y}")
                        }
                    }
                }

                detectVerticalDragGestures(
                    onVerticalDrag = { _, delta ->
                        println("GestureDetect: $delta")
//                        if (!(hasFooter || hasHeader)) {
//                            return@detectVerticalDragGestures
//                        }
//                        if (delta < 0 && isReachBottom() && hasFooter) {
//                            contentOffset =
//                                max(-footerHeight, (contentOffset + delta).roundToInt())
//                        } else if (delta > 0 && isReachTop() && hasHeader) {
//                            contentOffset =
//                                min(headerHeight, (contentOffset + delta).roundToInt())
//                        }
                    }
                )
            }
            /*.draggable(
                state = rememberDraggableState { delta ->
                    if (!(hasFooter || hasHeader)) {
                        return@rememberDraggableState
                    }
                    if (delta < 0 && isReachBottom() && hasFooter) {
                        contentOffset =
                            max(-footerHeight, (contentOffset + delta).roundToInt())
                    } else if (delta > 0 && isReachTop() && hasHeader) {
                        contentOffset =
                            min(headerHeight, (contentOffset + delta).roundToInt())
                    }
                },
                orientation = Orientation.Vertical,
                onDragStopped = { velocity ->
                    val scope = object : ScrollScope {
                        override fun scrollBy(pixels: Float): Float {
                            if (!isReachBottom()) {
                                return pixels
                            }
                            val newContentOffset =
                                max(-footerHeight.toFloat(), contentOffset + pixels)
                            val consumed = newContentOffset - contentOffset
                            contentOffset = newContentOffset.roundToInt()
                            return consumed
                        }
                    }
                    with(scope) {
                        with(fling) {
                            coroutineScope.launch {
                                performFling(velocity)
                            }
                        }
                    }
                }
            )*//*.scrollable(
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
                        if (contentOffset < 0 || isReachBottom()) {
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
                        if (contentOffset > 0 || isReachTop()) {
                            contentOffset =
                                min(headerHeight, (contentOffset + delta).roundToInt())
                        }
                        return@rememberScrollableState delta
                    }
                }
            )
            */
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Preview
@Composable
fun PreviewRefreshLayout() {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scrollState = rememberScrollState()
    val childState = rememberLazyStaggeredGridState()
    val flingBehavior = ScrollableDefaults.flingBehavior()

    val coroutineScope = rememberCoroutineScope()

    val nestedCollection = remember(scrollState, childState) {
        object : NestedScrollConnection {

            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (childState.firstVisibleItemIndex == 0 && childState.firstVisibleItemScrollOffset == 0 && available.y < 0) {
                    return if (scrollState.value < scrollState.maxValue) {
                        coroutineScope.launch {
                            scrollState.scrollBy(-available.y)
                        }
                        available
                    } else {
                        Offset.Zero
                    }
                }
                if (childState.firstVisibleItemIndex == 0 && childState.firstVisibleItemScrollOffset == 0 && available.y > 0) {
                    coroutineScope.launch {
                        scrollState.scrollBy(-available.y)
                    }
                    return available
                }
                return super.onPreScroll(available, source)
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                if (available.y != 0f && childState.firstVisibleItemIndex == 0 && childState.firstVisibleItemScrollOffset == 0) {
                    coroutineScope.launch {
                        scrollState.scroll {
                            with(flingBehavior) {
                                performFling(-available.y)
                            }
                        }
                    }
                }
                return super.onPostFling(consumed, available)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "主页")
                },
                scrollBehavior = scrollBehavior
            )
        },
//        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {

        BoxWithConstraints(modifier = Modifier
            .padding(it)
            .fillMaxSize()) {

            Column(
                modifier = Modifier
                    .nestedScroll(nestedCollection)
                    .verticalScroll(
                        state = scrollState,
                        enabled = false
                    )
                    .wrapContentHeight(unbounded = true)
            ) {

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .background(Color.Red))

                CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
                    LazyVerticalStaggeredGrid(
                        state = childState,
                        columns = StaggeredGridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier.height(
                            this@BoxWithConstraints.maxHeight
                        )
                    ) {

                        items(30) { index ->

                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .height((300..350).random().dp)
                            ) {
                                Text(text = "$index", modifier = Modifier.align(Alignment.Center))
                            }

                        }

                    }
                }
            }
            
        }
        
    }
    
}