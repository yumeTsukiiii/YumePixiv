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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.nestedScrollable(
    nestedScrollableState: NestedScrollableState,
    orientation: Orientation,
    overscrollEffect: OverscrollEffect? = null,
) = this.scrollable(
    state = nestedScrollableState,
    orientation = orientation,
    overscrollEffect = overscrollEffect
).run {
    if (overscrollEffect != null) {
        this
            .overscroll(overscrollEffect)
            .onSizeChanged {
                overscrollEffect.isEnabled = if (orientation == Orientation.Vertical) {
                    it.height != 0
                } else {
                    it.width != 0
                }
            }
    } else {
        this
    }
}

@Composable
internal fun rememberNestedScrollableState(
    parentScrollState: ScrollableState,
    childScrollState: ScrollableState,
    onScroll: ((delta: Float) -> Unit)? = null,
    isChildReachTop: () -> Boolean,
): NestedScrollableState {
    val scrollableState = rememberScrollableState { delta ->
        onScroll?.invoke(delta)
        if (isChildReachTop()) {
            val parentConsumed = -parentScrollState.dispatchRawDelta(-delta)
            // 父容器消费不完 delta，剩余滚动传递给子容器
            if (parentConsumed != delta) {
                childScrollState.dispatchRawDelta(parentConsumed - delta)
            }
            if (delta < 0) {
                // 向上滑动时，需要保持滑动速度，传递到子容器，直接全部消费 delta
                delta
            } else {
                // 向下滑动时，需要滚动到顶部时处理持续滑动手势，随父容器走
                parentConsumed
            }
        } else {
            // 用 dispatchRaw，scrollBy 是挂起函数，直接使用手势分发 delta 即可
            val consumed = -childScrollState.dispatchRawDelta(-delta)
            if (delta < 0) {
                // 向上滑动时，需要滚动到底部时处理持续滑动手势，随子容器走
                consumed
            } else {
                // 向下滑动时，需要保持滑动速度，传递到父容器，直接全部消费 delta
                delta
            }
        }
    }
    return object : NestedScrollableState, ScrollableState by scrollableState {}
}

interface NestedScrollableState : ScrollableState