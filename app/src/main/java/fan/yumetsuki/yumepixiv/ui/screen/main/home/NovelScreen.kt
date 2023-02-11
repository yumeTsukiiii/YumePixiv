package fan.yumetsuki.yumepixiv.ui.screen.main.home

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.request.ImageRequest
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import fan.yumetsuki.yumepixiv.ui.components.*
import fan.yumetsuki.yumepixiv.ui.screen.main.components.IllustDetail
import fan.yumetsuki.yumepixiv.ui.screen.main.components.IllustDetailImage
import fan.yumetsuki.yumepixiv.ui.screen.main.components.IllustDetailScreen
import fan.yumetsuki.yumepixiv.ui.screen.main.components.IllustDetailTag
import fan.yumetsuki.yumepixiv.utils.pixivImageRequestBuilder
import fan.yumetsuki.yumepixiv.viewmodels.MangaViewModel
import fan.yumetsuki.yumepixiv.viewmodels.NovelViewModel
import fan.yumetsuki.yumepixiv.viewmodels.isOpenIllustDetail

@Composable
private fun imageRequestBuilder(imageUrl: String): ImageRequest.Builder {
    return pixivImageRequestBuilder(imageUrl = imageUrl)
        .crossfade(500)
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun NovelScreen(
    modifier: Modifier = Modifier,
    viewModel: NovelViewModel = hiltViewModel()
) {

    val screenState by viewModel.uiState.collectAsState()

    val refreshLayoutState = rememberRefreshLayoutState(
        showHeader = false,
        showFooter = screenState.isLoadMore
    )
    val parentScrollState = rememberScrollState()
    val childScrollState = rememberLazyListState()
    val nestedScrollableState = rememberNestedScrollableState(
        parentScrollState = parentScrollState,
        childScrollState = childScrollState
    ) {
        childScrollState.firstVisibleItemIndex == 0 && childScrollState.firstVisibleItemScrollOffset == 0
    }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = screenState.isReLoading)

    val firstVisibleItemIndex by remember {
        derivedStateOf {
            childScrollState.firstVisibleItemIndex
        }
    }
    val totalVisibleSize by remember {
        derivedStateOf {
            childScrollState.layoutInfo.visibleItemsInfo.size
        }
    }
    val totalListSize by remember {
        derivedStateOf {
            childScrollState.layoutInfo.totalItemsCount
        }
    }

    LaunchedEffect(Unit) {
        viewModel.reloadNovelsIfEmpty()
    }

    // TODO 抽离 StateRefreshLayout，封装 refresh 和 load 状态
    // TODO 添加 FloatActionButton 回到顶部
    // TODO 添加 showFooter / showHeader 能力，外部处于 load / refresh 状态时才允许显示，在 StateRefreshLayout 里做
    LaunchedEffect(
        firstVisibleItemIndex,
        totalVisibleSize,
        totalListSize
    ) {
        if (
            childScrollState.layoutInfo.visibleItemsInfo.any {
                it.index == totalListSize - totalVisibleSize
            }
        ) {
            viewModel.nextPageNovel()
        }
    }

    // FIXME 偶现没有向上滚动一些
    LaunchedEffect(screenState.isLoadMore) {
        if (!screenState.isLoadMore) {
            if (refreshLayoutState.contentOffset.value != 0) {
                // nested scrollBy 需要反方向处理
                // 向上滑动到底，delta 是 负数; scrollBy 调用时，支持传递的 value 到 delta
                nestedScrollableState.scrollBy(refreshLayoutState.contentOffset.value.toFloat())
            }
        }
    }

    SwipeRefresh(state = swipeRefreshState, onRefresh = {
        viewModel.reloadNovels()
    }) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (screenState.rankingNovels.isEmpty() && screenState.novels.isEmpty() && !screenState.isReLoading) {
                Box(modifier = modifier) {
                    // TODO UI 修改
                    Text(text = "没有数据欸！！！")
                }
            } else if (screenState.rankingNovels.isNotEmpty() && screenState.novels.isNotEmpty()) {
                RefreshLayout(
                    scrollBehaviour = RefreshLayoutDefaults.flingScrollBehaviour(
                        isReachTop = { false },
                        isReachBottom = {
                            childScrollState.layoutInfo.visibleItemsInfo.find { item ->
                                item.index == childScrollState.layoutInfo.totalItemsCount - 1 && item.offset + item.size <= childScrollState.layoutInfo.viewportEndOffset
                            } != null
                        },
                        state = refreshLayoutState,
                    ),
                    modifier = Modifier.fillMaxSize(),
                    footer = {
                        LoadMore(modifier = Modifier.fillMaxWidth()) {
                            Text(text = "加载中")
                        }
                    },
                ) {
                    BoxWithConstraints {

                        Column(
                            modifier = Modifier
                                .nestedScrollable(
                                    nestedScrollableState = nestedScrollableState,
                                    orientation = Orientation.Vertical
                                )
                                .verticalScroll(
                                    parentScrollState,
                                    enabled = false
                                )
                                .wrapContentHeight(
                                    align = Alignment.Top,
                                    unbounded = true
                                )
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

                            if (screenState.rankingNovels.isNotEmpty()) {
                                // TODO else 展示
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(8.dp),
                                ) {
                                    itemsIndexed(screenState.rankingNovels) { index, rankingNovel ->
                                        NovelRankingCard(
                                            imageUrl = rankingNovel.coverImageUrl ?: TODO("默认图片"),
                                            author = rankingNovel.author,
                                            title = rankingNovel.title,
                                            authorAvatar = rankingNovel.authorAvatarUrl ?: TODO("默认图片"),
                                            wordCount = rankingNovel.wordCount,
                                            imageRequestBuilder = imageRequestBuilder(rankingNovel.coverImageUrl),
                                            avatarImageRequestBuilder = imageRequestBuilder(rankingNovel.authorAvatarUrl),
                                            modifier = Modifier.size(rankingNovel.cardHeight),
                                            isFavorite = rankingNovel.isBookmark,
                                            tags = rankingNovel.tags.map { tag -> "#${tag.translateName ?: tag.name}" },
                                            onFavoriteClick = {
                                                viewModel.changeRankingNovelBookmark(index)
                                            },
                                            onClick = {
                                                // TODO
                                            }
                                        )
                                    }
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

                            if (screenState.novels.isNotEmpty()) {
                                // TODO else 展示
                                LazyColumn(
                                    contentPadding = PaddingValues(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.height(this@BoxWithConstraints.maxHeight),
                                    state = childScrollState,
                                    userScrollEnabled = false
                                ) {
                                    itemsIndexed(screenState.novels) { index, novel ->
                                        NovelCard(
                                            imageUrl = novel.coverImageUrl ?: TODO("默认图片"),
                                            modifier = Modifier.height(novel.cardHeight).clickable {
                                                   // TODO
                                            },
                                            isBookmark = novel.isBookmark,
                                            bookmarks = novel.totalBookmarks,
                                            title = novel.title,
                                            author = novel.author,
                                            authorAvatarUrl = novel.authorAvatarUrl ?: TODO("默认图片"),
                                            wordCount = novel.wordCount,
                                            series = novel.series,
                                            tags = novel.tags.map { tag -> "#${tag.translateName ?: tag.name}" },
                                            imageRequestBuilder = imageRequestBuilder(novel.coverImageUrl),
                                            avatarImageRequestBuilder = imageRequestBuilder(novel.authorAvatarUrl),
                                            onBookmarkClick = {
                                                viewModel.changeNovelBookmark(index)
                                            },
                                            onSeriesClick = {}
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}