package fan.yumetsuki.yumepixiv.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.overscroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.nestedScrollable(
    state: NestedScrollableState,
    orientation: Orientation,
    enabled: Boolean = true,
    overscrollEffect: OverscrollEffect? = null
) = this.scrollable(
    state = state,
    enabled = enabled,
    orientation = orientation,
    overscrollEffect = overscrollEffect
).run {
    if (overscrollEffect != null) {
        this
            .overscroll(overscrollEffect)
            .onSizeChanged {
                overscrollEffect.isEnabled = it.height != 0
            }
    } else {
        this
    }
}

@Composable
fun rememberNestedScrollableState(
    parentScrollState: ScrollableState,
    childScrollState: ScrollableState,
    isChildReachTop: () -> Boolean,
) : NestedScrollableState {
    val scrollableState = rememberScrollableState { delta ->
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
    return remember(scrollableState) {
        NestedScrollableState(scrollableState)
    }
}

class NestedScrollableState(
    delegate: ScrollableState
) : ScrollableState by delegate