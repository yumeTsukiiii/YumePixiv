package fan.yumetsuki.yumepixiv.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState

@OptIn(ExperimentalFoundationApi::class)
fun LazyStaggeredGridState.isVerticalReachBottom(): Boolean {
    return layoutInfo.visibleItemsInfo.any { visibleItem ->
        visibleItem.size.height + visibleItem.offset.y <= layoutInfo.viewportEndOffset
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun LazyStaggeredGridState.isHorizontalReachEnd(): Boolean {
    return layoutInfo.visibleItemsInfo.any { visibleItem ->
        visibleItem.size.width + visibleItem.offset.x <= layoutInfo.viewportEndOffset
    }
}