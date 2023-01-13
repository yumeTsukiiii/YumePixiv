package fan.yumetsuki.yumepixiv.ui.screen.main.home

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.request.ImageRequest
import fan.yumetsuki.yumepixiv.ui.components.IllustCard
import fan.yumetsuki.yumepixiv.ui.components.IllustRankCard
import fan.yumetsuki.yumepixiv.ui.components.nestedScrollable
import fan.yumetsuki.yumepixiv.utils.pixivImageRequestBuilder
import fan.yumetsuki.yumepixiv.viewmodels.IllustViewModel

@Composable
fun imageRequestBuilder(imageUrl: String): ImageRequest.Builder {
    return pixivImageRequestBuilder(imageUrl = imageUrl)
        .crossfade(500)
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun IllustScreen(
    modifier: Modifier = Modifier,
    viewModel: IllustViewModel = hiltViewModel()
) {

    val screenState by viewModel.uiState.collectAsState()

    val parentScrollState = rememberScrollState()
    val childScrollState = rememberLazyStaggeredGridState()

    LaunchedEffect(Unit) {
        viewModel.refreshIllusts()
    }

    if (screenState.isLoading) {
        Box(modifier = modifier) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    } else if (screenState.rankingIllust.isEmpty() && screenState.illusts.isEmpty()) {
        Box(modifier = modifier) {
            // TODO UI 修改
            Text(text = "没有数据欸！！！")
        }
    } else {
        BoxWithConstraints(
            modifier = modifier.nestedScrollable(
                parentScrollState = parentScrollState,
                childScrollState = childScrollState,
                orientation = Orientation.Vertical,
                overscrollEffect = ScrollableDefaults.overscrollEffect()
            ) {
                childScrollState.firstVisibleItemIndex == 0 && childScrollState.firstVisibleItemScrollOffset == 0
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
                        items(screenState.rankingIllust) { rankingIllust ->
                            IllustRankCard(
                                imageUrl = rankingIllust.imageUrl ?: TODO("默认图片"),
                                author = rankingIllust.author,
                                title = rankingIllust.title,
                                pageCount = rankingIllust.pageCount,
                                authorAvatar = rankingIllust.authorAvatar ?: TODO("默认图片"),
                                imageRequestBuilder = imageRequestBuilder(rankingIllust.imageUrl),
                                avatarImageRequestBuilder = imageRequestBuilder(rankingIllust.imageUrl),
                                modifier = Modifier.size(156.dp)
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
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(this@BoxWithConstraints.maxHeight),
                        state = childScrollState,
                        userScrollEnabled = false
                    ) {
                        items(screenState.illusts) { illust ->
                            IllustCard(
                                imageUrl = illust.imageUrl ?: TODO("默认图片"),
                                pageCount = illust.pageCount,
                                modifier = Modifier.height(illust.height),
                                imageRequestBuilder = imageRequestBuilder(illust.imageUrl)
                            )
                        }
                    }
                }
            }
        }
    }
}