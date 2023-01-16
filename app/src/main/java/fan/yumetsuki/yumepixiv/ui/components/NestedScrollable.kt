package fan.yumetsuki.yumepixiv.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Velocity
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