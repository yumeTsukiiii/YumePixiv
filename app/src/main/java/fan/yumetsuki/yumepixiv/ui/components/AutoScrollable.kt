package fan.yumetsuki.yumepixiv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.roundToInt


@Composable
inline fun <reified T> AutoLazyVerticalGrid(
    items: List<T>,
    columns: GridCells,
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(0.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(0.dp),
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    autoScrollDelayTimeMillis: Long = 10,
    scrollOffsetPerMillis: Float = 1f,
    noinline key: ((item: T) -> Any)? = null,
    noinline span: (LazyGridItemSpanScope.(item: T) -> GridItemSpan)? = null,
    noinline contentType: (item: T) -> Any? = { null },
    crossinline itemContent: @Composable T.() -> Unit
) {

    var innerStateData by remember(items) {
        mutableStateOf(listOf(*items.toTypedArray()))
    }

    val firstVisibleIndex by derivedStateOf {
        state.firstVisibleItemIndex
    }

    val firstVisibleOffset by derivedStateOf {
        state.firstVisibleItemScrollOffset
    }

    LaunchedEffect(firstVisibleIndex, firstVisibleOffset, innerStateData) {
        state.scrollBy(scrollOffsetPerMillis)
        delay(autoScrollDelayTimeMillis)
        if (firstVisibleIndex <= 0) {
            return@LaunchedEffect
        }
        innerStateData = innerStateData.subList(firstVisibleIndex, innerStateData.size) + innerStateData.subList(0, firstVisibleIndex)
        state.scrollToItem(0, max(0f, firstVisibleOffset - scrollOffsetPerMillis).roundToInt())
    }

    LazyVerticalGrid(
        columns = columns,
        modifier = modifier,
        state = state,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalArrangement = horizontalArrangement,
        flingBehavior = flingBehavior,
        userScrollEnabled = false
    ) {

        items(innerStateData, key, span, contentType) { item ->
            itemContent(item)
        }

    }

}

@Preview
@Composable
fun AutoLazyVerticalGridPreview() {

    AutoLazyVerticalGrid(
        items = listOf(Color.Blue, Color.Red, Color.Gray, Color.Green, Color.Yellow, Color.Magenta),
        columns = GridCells.Fixed(2),
    ) {

        Box(modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .background(this))

    }

}
