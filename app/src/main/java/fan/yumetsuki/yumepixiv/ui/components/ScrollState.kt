package fan.yumetsuki.yumepixiv.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState

@OptIn(ExperimentalFoundationApi::class)
fun LazyStaggeredGridState.isVerticalReachBottom(column: Int): Boolean {
    return layoutInfo.visibleItemsInfo.any { visibleItem ->
        visibleItem.index >= layoutInfo.totalItemsCount - column && visibleItem.size.height + visibleItem.offset.y <= layoutInfo.viewportEndOffset
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun LazyStaggeredGridState.isHorizontalReachEnd(row: Int): Boolean {
    return layoutInfo.visibleItemsInfo.any { visibleItem ->
        visibleItem.index >= layoutInfo.totalItemsCount - row && visibleItem.size.width + visibleItem.offset.x <= layoutInfo.viewportEndOffset
    }
}