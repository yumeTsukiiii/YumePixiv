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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import fan.yumetsuki.yumepixiv.viewmodels.IllustViewModel
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
fun IllustScreen(
    modifier: Modifier = Modifier,
    viewModel: IllustViewModel = hiltViewModel()
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
    val childScrollState = rememberLazyStaggeredGridState()
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

    // TODO ?????? StateRefreshLayout????????? refresh ??? load ??????
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

    // FIXME ??????????????????????????????
    LaunchedEffect(screenState.isLoadMore, screenState.isError) {
        if (!screenState.isLoadMore && !screenState.isError) {
            if (refreshLayoutState.contentOffset.value != 0) {
                // nested scrollBy ?????????????????????
                // ?????????????????????delta ??? ??????; scrollBy ??????????????????????????? value ??? delta
                nestedScrollableState.scrollBy(refreshLayoutState.contentOffset.value.toFloat())
            }
        }
    }

    // FIXME Material3 ???????????? SwipeRefresh??????????????? Material2?????????
    Scaffold(
        floatingActionButton = {
            // ??? Scaffold ?????? Animated ??? Floating fadeIn ????????????????????? scaleIn????????? Bug ???
            AnimatedVisibility(
                visible = reachTopVisible,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                FloatingActionButton(onClick = {
                    coroutineScope.launch {
                        childScrollState.scrollToItem(0)
                        parentScrollState.scrollTo(0)
                        reachTopVisible = false
                    }
                }) {
                    Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = null)
                }
            }
        },
        modifier = modifier
    ) {
        SwipeRefresh(
            modifier = Modifier.padding(it),
            state = swipeRefreshState,
            onRefresh = {
                viewModel.reloadIllusts()
            },
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (screenState.rankingIllust.isEmpty() && screenState.illusts.isEmpty() && !screenState.isReLoading) {
                    NoDataTip(isError = screenState.isError)
                } else if (screenState.rankingIllust.isNotEmpty() && screenState.illusts.isNotEmpty()) {
                    RefreshLayout(
                        scrollBehaviour = RefreshLayoutDefaults.flingScrollBehaviour(
                            isReachTop = { false },
                            isReachBottom = {
                                childScrollState.isVerticalReachBottom(2)
                            },
                            state = refreshLayoutState,
                        ),
                        modifier = Modifier.fillMaxSize(),
                        footer = {
                            LoadMoreFooter(
                                isError = screenState.isError,
                                modifier = Modifier.clickable {
                                    viewModel.nextPageIllust()
                                }
                            )
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
                                        Text(text = "?????????")
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
                                    // TODO else ??????
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        contentPadding = PaddingValues(8.dp),
                                    ) {
                                        itemsIndexed(screenState.rankingIllust) { index, rankingIllust ->
                                            IllustRankCard(
                                                imageUrl = rankingIllust.coverImageUrl ?: TODO("????????????"),
                                                author = rankingIllust.author,
                                                title = rankingIllust.title,
                                                pageCount = rankingIllust.pageCount,
                                                authorAvatar = rankingIllust.authorAvatarUrl ?: TODO("????????????"),
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
                                        Text(text = "????????????")
                                    },
                                )

                                if (screenState.illusts.isNotEmpty()) {
                                    // TODO else ??????
                                    LazyVerticalStaggeredGrid(
                                        columns = StaggeredGridCells.Fixed(2),
                                        contentPadding = PaddingValues(8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.height(this@BoxWithConstraints.maxHeight),
                                        state = childScrollState,
                                        userScrollEnabled = false
                                    ) {
                                        itemsIndexed(screenState.illusts) { index, illust ->
                                            IllustCard(
                                                imageUrl = illust.coverImageUrl ?: TODO("????????????"),
                                                pageCount = illust.pageCount,
                                                modifier = Modifier.height(illust.cardHeight),
                                                imageRequestBuilder = imageRequestBuilder(illust.coverImageUrl),
                                                isFavorite = illust.isBookmark,
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
                                        authorAvatarUrl = illust.authorAvatarUrl ?: TODO("????????????"),
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