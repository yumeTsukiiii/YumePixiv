package fan.yumetsuki.yumepixiv.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import fan.yumetsuki.yumepixiv.ui.foundation.detectVerticalDragGesturesIgnoreConsumed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun Modifier.nestedVerticalScroll(
    state: ScrollState,
    isChildReachTop: () -> Boolean,
    flingBehavior: FlingBehavior? = null,
    // TODO 支持反向
//    reverseScrolling: Boolean = false
): Modifier = composed {
    val fling = flingBehavior ?: ScrollableDefaults.flingBehavior()
    val isChildReachTopState = rememberUpdatedState(newValue = isChildReachTop)
    val coroutineScope = rememberCoroutineScope()
    val nestedCollection = remember(state, flingBehavior, coroutineScope) {
        nestedVerticalScrollConnection(state, coroutineScope, fling, isChildReachTopState.value)
    }
    this.nestedScroll(nestedCollection)
}.verticalScroll(
    state,
    enabled = false,
    flingBehavior = flingBehavior
)

internal fun nestedVerticalScrollConnection(
    state: ScrollState,
    coroutineScope: CoroutineScope,
    fling: FlingBehavior,
    isChildReachTop: () -> Boolean
): NestedScrollConnection {
    return object : NestedScrollConnection {

        override fun onPreScroll(
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            println("TestDelta: nested preScroll $available")
            if (isChildReachTop() && available.y < 0) {
                return if (state.value < state.maxValue) {
                    coroutineScope.launch {
                        state.scrollBy(-available.y)
                    }
                    available
                } else {
                    Offset.Zero
                }
            }
            if (isChildReachTop() && available.y > 0) {
                coroutineScope.launch {
                    state.scrollBy(-available.y)
                }
                return available
            }
            return super.onPreScroll(available, source)
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            if (available.y != 0f && isChildReachTop()) {
                coroutineScope.launch {
                    state.scroll {
                        with(fling) {
                            performFling(-available.y)
                        }
                    }
                }
            }
            return super.onPostFling(consumed, available)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Preview
@Composable
fun PreviewNestedScrollable() {

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