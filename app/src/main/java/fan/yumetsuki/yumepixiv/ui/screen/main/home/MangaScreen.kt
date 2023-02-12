package fan.yumetsuki.yumepixiv.ui.screen.main.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
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
import fan.yumetsuki.yumepixiv.ui.screen.main.components.*
import fan.yumetsuki.yumepixiv.utils.pixivImageRequestBuilder
import fan.yumetsuki.yumepixiv.viewmodels.MangaViewModel
import fan.yumetsuki.yumepixiv.viewmodels.isOpenIllustDetail
import kotlinx.coroutines.launch

@Composable
private fun imageRequestBuilder(imageUrl: String): ImageRequest.Builder {
    return pixivImageRequestBuilder(imageUrl = imageUrl)
        .crossfade(500)
}

@Suppress("DEPRECATION")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class
)
@Composable
fun MangaScreen(
    modifier: Modifier = Modifier,
    viewModel: MangaViewModel = hiltViewModel()
) {

    var reachTopVisible by remember {
        mutableStateOf(false)
    }
    val screenState by viewModel.uiState.collectAsState()

    val refreshLayoutState = rememberRefreshLayoutState(
        showHeader = false,
        showFooter = screenState.isLoadMore
    )
    val parentScrollState = rememberScrollState()
    val childScrollState = rememberLazyGridState()
    val nestedScrollableState = rememberNestedScrollableState(
        parentScrollState = parentScrollState,
        childScrollState = childScrollState,
        onScroll = { delta ->
            if (delta != 0f) {
                reachTopVisible = !(
                    childScrollState.firstVisibleItemIndex == 0 && childScrollState.firstVisibleItemScrollOffset == 0
                ) && delta > 0
            }
        }
    ) {
        childScrollState.firstVisibleItemIndex == 0 && childScrollState.firstVisibleItemScrollOffset == 0
    }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = screenState.isReLoading)
    val coroutineScope = rememberCoroutineScope()

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
        viewModel.reloadIllustsIfEmpty()
    }

    // TODO 抽离 StateRefreshLayout，封装 refresh 和 load 状态
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
            viewModel.nextPageIllust()
        }
    }

    // FIXME 偶现没有向上滚动一些
    LaunchedEffect(screenState.isLoadMore, screenState.isError) {
        if (!screenState.isLoadMore && !screenState.isError) {
            if (refreshLayoutState.contentOffset.value != 0) {
                // nested scrollBy 需要反方向处理
                // 向上滑动到底，delta 是 负数; scrollBy 调用时，支持传递的 value 到 delta
                nestedScrollableState.scrollBy(refreshLayoutState.contentOffset.value.toFloat())
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            // 在 Scaffold 中用 Animated 和 Floating fadeIn 动画失效，换成 scaleIn，官方 Bug 吧
            AnimatedVisibility(
                visible = reachTopVisible,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                FloatingActionButton(onClick = {
                    coroutineScope.launch {
                        childScrollState.scrollToItem(0)
                        parentScrollState.scrollTo(0)
                    }
                }) {
                    Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = null)
                }
            }
        },
        modifier = modifier
    ) {
        SwipeRefresh(state = swipeRefreshState, modifier = Modifier.padding(it), onRefresh = {
            viewModel.reloadIllusts()
        }) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (screenState.rankingIllust.isEmpty() && screenState.illusts.isEmpty() && !screenState.isReLoading) {
                    NoDataTip(isError = screenState.isError)
                } else if (screenState.rankingIllust.isNotEmpty() && screenState.illusts.isNotEmpty()) {
                    RefreshLayout(
                        scrollBehaviour = RefreshLayoutDefaults.flingScrollBehaviour(
                            isReachTop = { false },
                            isReachBottom = {
                                childScrollState.layoutInfo.visibleItemsInfo.find { item ->
                                    item.index == childScrollState.layoutInfo.totalItemsCount - 1 && item.offset.y + item.size.height <= childScrollState.layoutInfo.viewportEndOffset
                                } != null
                            },
                            state = refreshLayoutState,
                        ),
                        modifier = Modifier.fillMaxSize(),
                        footer = {
                            LoadMoreFooter(
                                isError = screenState.isError,
                                modifier = Modifier.clickable {
                                    viewModel.nextPageIllust()
                                })
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

                                if (screenState.rankingIllust.isNotEmpty()) {
                                    // TODO else 展示
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        contentPadding = PaddingValues(8.dp),
                                    ) {
                                        itemsIndexed(screenState.rankingIllust) { index, rankingIllust ->
                                            MangaRankCard(
                                                imageUrl = rankingIllust.coverImageUrl ?: TODO("默认图片"),
                                                author = rankingIllust.author,
                                                title = rankingIllust.title,
                                                pageCount = rankingIllust.pageCount,
                                                authorAvatar = rankingIllust.authorAvatarUrl ?: TODO("默认图片"),
                                                imageRequestBuilder = imageRequestBuilder(rankingIllust.coverImageUrl),
                                                avatarImageRequestBuilder = imageRequestBuilder(rankingIllust.authorAvatarUrl),
                                                modifier = Modifier.size(rankingIllust.cardHeight),
                                                isFavorite = rankingIllust.isBookmark,
                                                onFavoriteClick = {
                                                    viewModel.changeRankingIllustBookmark(index)
                                                },
                                                onClick = {
                                                    viewModel.openRankingIllustDetail(index)
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

                                if (screenState.illusts.isNotEmpty()) {
                                    // TODO else 展示
                                    LazyVerticalGrid(
                                        columns = GridCells.Fixed(2),
                                        contentPadding = PaddingValues(8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.height(this@BoxWithConstraints.maxHeight),
                                        state = childScrollState,
                                        userScrollEnabled = false
                                    ) {
                                        itemsIndexed(screenState.illusts) { index, illust ->
                                            MangaCard(
                                                imageUrl = illust.coverImageUrl ?: TODO("默认图片"),
                                                pageCount = illust.pageCount,
                                                modifier = Modifier.height(illust.cardHeight),
                                                imageRequestBuilder = imageRequestBuilder(illust.coverImageUrl),
                                                isFavorite = illust.isBookmark,
                                                favoriteCount = illust.totalBookmarks,
                                                title = illust.title,
                                                tags = illust.tags.map { tag -> "#${tag.name}" },
                                                onFavoriteClick = {
                                                    viewModel.changeIllustBookmark(index)
                                                },
                                                onClick = {
                                                    viewModel.openIllustDetail(index)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (screenState.isOpenIllustDetail) {
                        Dialog(
                            onDismissRequest = {
                                viewModel.closeIllustDetail()
                            },
                            properties = DialogProperties(
                                dismissOnBackPress = true,
                                usePlatformDefaultWidth = false
                            )
                        ) {
                            IllustDetailScreen(
                                initialPage = screenState.currentIllustPage,
                                illustDetails = screenState.currentSelectIllusts?.map { illust ->
                                    IllustDetail(
                                        title = illust.title,
                                        caption = illust.caption,
                                        images = illust.metaImages.map { url ->
                                            IllustDetailImage(url, illust.height.toFloat() / illust.width)
                                        },
                                        author = illust.author,
                                        authorAvatarUrl = illust.authorAvatarUrl ?: TODO("默认图片"),
                                        isBookmark = illust.isBookmark,
                                        totalViews = illust.totalViews,
                                        totalBookmark = illust.totalBookmarks,
                                        createDate = illust.createDate,
                                        tags = illust.tags.map { tag ->
                                            IllustDetailTag(tag.name, tag.translateName)
                                        }
                                    )
                                } ?: emptyList(),
                                onCloseClick = {
                                    viewModel.closeIllustDetail()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

}