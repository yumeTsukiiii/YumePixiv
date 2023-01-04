package fan.yumetsuki.yumepixiv.ui.components.nested

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.overscroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun <P: ScrollableState, C: ScrollableState> NestedScrollable(
    modifier: Modifier,
    parentScrollState: P,
    childScrollState: C,
    isChildReachTop: (parentScrollState: P, childScrollState: C, delta: Float) -> Boolean,
    content: @Composable BoxWithConstraintsScope.() -> Unit
) {
    val overscrollEffect = ScrollableDefaults.overscrollEffect()

    BoxWithConstraints(
        modifier = modifier.scrollable(
            state = rememberScrollableState { delta ->
                if (isChildReachTop(parentScrollState, childScrollState, delta)) {
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
            },
            orientation = Orientation.Vertical,
            overscrollEffect = overscrollEffect
        ).overscroll(overscrollEffect)
            .onSizeChanged {
                // 见 verticalColumn 修饰符，Column 的 overscroll.isEnable 直接由高度决定
                overscrollEffect.isEnabled = it.height != 0
            }
    ) {
        content()
    }
}

class NestedScrollableScope()

fun NestedScrollableScope.parentColumn() {

}

fun NestedScrollableScope.childLazyStaggeredGrid() {

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NestedScrollable(
    modifier: Modifier,
    content: NestedScrollableScope.() -> Unit
) {

}