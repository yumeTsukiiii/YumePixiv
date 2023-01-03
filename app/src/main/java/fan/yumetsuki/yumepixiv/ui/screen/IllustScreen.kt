package fan.yumetsuki.yumepixiv.ui.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import fan.yumetsuki.yumepixiv.ui.components.IllustCard
import fan.yumetsuki.yumepixiv.ui.components.IllustRankCard

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun IllustScreen(
    modifier: Modifier = Modifier
) {

    val overscrollEffect = ScrollableDefaults.overscrollEffect()
    val parentScrollState = rememberScrollState()
    val childScrollState = rememberLazyStaggeredGridState()

    BoxWithConstraints(
        modifier = modifier.scrollable(
            state = rememberScrollableState { delta ->
                if (childScrollState.firstVisibleItemIndex == 0 && childScrollState.firstVisibleItemScrollOffset == 0) {
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

        Column(
            modifier = Modifier
                .verticalScroll(parentScrollState, enabled = false)
                .wrapContentHeight(
                    align = Alignment.Top,
                    unbounded = true
                )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                ListItem(
                    leadingContent = {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null)
                    },
                    headlineText = {
                        Text(text = "排行榜")
                    },
                    trailingContent = {
                        Row {
                            IconButton(
                                onClick = {}
                            ) {
                                Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = null)
                            }
                        }
                    }
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(8.dp),
                ) {
                    items(10) {
                        IllustRankCard(
                            imageUrl = "https://tse4-mm.cn.bing.net/th/id/OIP-C.P5Y9Ph3AUf7NSr9GzYDHjAHaEo?w=280&h=180&c=7&r=0&o=5&dpr=2&pid=1.7",
                            author = "二阶堂梦月",
                            title = "如月澪",
                            pageCount = (1..9).random(),
                            authorAvatar = "https://tse4-mm.cn.bing.net/th/id/OIP-C.P5Y9Ph3AUf7NSr9GzYDHjAHaEo?w=280&h=180&c=7&r=0&o=5&dpr=2&pid=1.7",
                            modifier = Modifier.size(156.dp)
                        )
                    }
                }

                ListItem(
                    leadingContent = {
                        Icon(imageVector = Icons.Default.Favorite, contentDescription = null)
                    },
                    headlineText = {
                        Text(text = "为你推荐")
                    },
                )
            }

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(this@BoxWithConstraints.maxHeight),
                state = childScrollState,
                userScrollEnabled = false
            ) {
                items(100) {
                    IllustCard(
                        imageUrl = "https://tse4-mm.cn.bing.net/th/id/OIP-C.P5Y9Ph3AUf7NSr9GzYDHjAHaEo?w=280&h=180&c=7&r=0&o=5&dpr=2&pid=1.7",
                        pageCount = (1..9).random(),
                        modifier = Modifier.height((200..300).random().dp)
                    )
                }
            }
        }
    }
}