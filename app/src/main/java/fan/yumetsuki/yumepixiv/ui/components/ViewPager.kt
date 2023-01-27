package fan.yumetsuki.yumepixiv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun rememberLazyViewPagerState() : LazyViewPagerState {
    val selectedPageIndexState = remember {
        mutableStateOf(0)
    }
    return remember {
        LazyViewPagerStateImpl(selectedPageIndexState)
    }
}

@Composable
fun LazyHorizontalViewPager(
    modifier: Modifier = Modifier,
    state: LazyViewPagerState = rememberLazyViewPagerState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    horizontalArrangement: Arrangement.Horizontal =
        if (!reverseLayout) Arrangement.Start else Arrangement.End,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    content: LazyListScope.() -> Unit
) {

    val contentState = rememberLazyListState()

    val coroutineScope = rememberCoroutineScope()

    var isDragging by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(isDragging, state.selectedPageIndex) {
        contentState.animateScrollToItem(state.selectedPageIndex)
    }

    BoxWithConstraints(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.draggable(
                state = rememberDraggableState { delta ->
                    coroutineScope.launch {
                        contentState.scrollBy(-delta)
                    }
                },
                onDragStarted = {
                    isDragging = true
                },
                onDragStopped = { velocity ->
                    isDragging = false
                    if (velocity > 300f) {
                        state.selectedPageIndex = (state.selectedPageIndex - 1).coerceIn(0, contentState.layoutInfo.totalItemsCount - 1)
                    } else if (velocity < -300f) {
                        state.selectedPageIndex = (state.selectedPageIndex + 1).coerceIn(0, contentState.layoutInfo.totalItemsCount - 1)
                    } else {
                        contentState.layoutInfo.visibleItemsInfo.find {
                            it.index == state.selectedPageIndex
                        }?.also { itemInfo ->
                            if (abs(itemInfo.offset) > itemInfo.size / 2) {
                                if (itemInfo.offset < 0) {
                                    state.selectedPageIndex = (state.selectedPageIndex + 1).coerceIn(0, contentState.layoutInfo.totalItemsCount - 1)
                                } else {
                                    state.selectedPageIndex = (state.selectedPageIndex - 1).coerceIn(0, contentState.layoutInfo.totalItemsCount - 1)
                                }
                            }
                        }
                    }
                },
                orientation = Orientation.Horizontal
            )
        ) {
            LazyRow(
                state = contentState,
                contentPadding = contentPadding,
                reverseLayout = reverseLayout,
                horizontalArrangement = horizontalArrangement,
                verticalAlignment = verticalAlignment,
                flingBehavior = flingBehavior,
                userScrollEnabled = false
            ) {
                content()
            }
        }
    }

}

interface LazyViewPagerState {

    var selectedPageIndex: Int

}

class LazyViewPagerStateImpl(
    selectedPageIndexState: MutableState<Int>
): LazyViewPagerState {

    override var selectedPageIndex by selectedPageIndexState

}

@Preview
@Composable
fun LazyHorizontalViewPagerPreview() {

    BoxWithConstraints {
        LazyHorizontalViewPager(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.Green)
        ) {

            items(arrayOf(Color.Red, Color.Yellow, Color.Gray, Color.Blue)) {
                Box(modifier = Modifier
                    .width(this@BoxWithConstraints.maxWidth)
                    .height(156.dp)
                    .background(it))
            }

        }
    }

}