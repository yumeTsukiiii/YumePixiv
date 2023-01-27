package fan.yumetsuki.yumepixiv.ui.screen.main.home

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import fan.yumetsuki.yumepixiv.data.AppRepository
import fan.yumetsuki.yumepixiv.network.appApiHttpClient
import fan.yumetsuki.yumepixiv.network.hashInterceptor
import fan.yumetsuki.yumepixiv.network.impl.KtorPixivAuthApi
import fan.yumetsuki.yumepixiv.network.impl.KtorPixivRecommendApi
import fan.yumetsuki.yumepixiv.network.oauthClient
import fan.yumetsuki.yumepixiv.network.tokenInterceptor
import fan.yumetsuki.yumepixiv.ui.components.YumePixivTip
import fan.yumetsuki.yumepixiv.ui.screen.main.components.IllustDetail
import fan.yumetsuki.yumepixiv.ui.screen.main.components.IllustDetailImage
import fan.yumetsuki.yumepixiv.ui.screen.main.components.IllustDetailTag
import fan.yumetsuki.yumepixiv.viewmodels.IllustViewModel



@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun IllustDetailScreen(
    viewModel: IllustViewModel = hiltViewModel()
) {

    val screenState by viewModel.uiState.collectAsState()
    var currentVisibleImageIndex by remember {
        mutableStateOf(screenState.currentIllustPage)
    }
    val pagerState = rememberPagerState(currentVisibleImageIndex)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                     IconButton(onClick = {
                         viewModel.closeIllustDetail()
                     }) {
                         Icon(imageVector = Icons.Default.Close, contentDescription = null)
                     }
                },
                title = {
                    Text(
                        text = if (screenState.isReLoading) {
                            "Loading..."
                        } else if (screenState.illusts.isEmpty() || screenState.illusts[pagerState.currentPage].metaImages.isEmpty()) {
                            "无图源？！"
                        } else {
                            "${currentVisibleImageIndex + 1} / ${screenState.illusts[pagerState.currentPage].metaImages.size}"
                        },
                        style = MaterialTheme.typography.titleSmall
                    )
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Filled.Share, contentDescription = null)
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                    navigationIconContentColor = Color.White,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 0.dp,
                    bottom = padding.calculateBottomPadding(),
                    start = padding.calculateStartPadding(LayoutDirection.Ltr),
                    end = padding.calculateEndPadding(LayoutDirection.Ltr)
                )
        ) {
            if (screenState.isReLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                if (screenState.illusts.isEmpty()) {
                    YumePixivTip(
                        text = "芜湖...好像好像什么东西也没有？！",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 96.dp)
                    )
                } else {
                    HorizontalPager(
                        state = pagerState,
                        count = screenState.illusts.size,
                        modifier = Modifier
                            .fillMaxSize()
                    ) { page ->

                        screenState.illusts[page].also { illust ->
                            IllustDetail(
                                title = illust.title,
                                caption = illust.caption,
                                images = illust.metaImages.map { metaImage ->
                                    IllustDetailImage(
                                        url = metaImage,
                                        ratio = illust.height.toFloat() / illust.width
                                    )
                                },
                                author = illust.author,
                                authorAvatarUrl = illust.authorAvatarUrl ?: TODO("默认图片"),
                                isBookmark = illust.isBookmark,
                                totalViews = illust.totalViews,
                                totalBookmark = illust.totalBookmarks,
                                createDate = illust.createDate,
                                tags = illust.tags.map {
                                    IllustDetailTag(it.name, it.translateName)
                                },
                                onPageChange = { page ->
                                    currentVisibleImageIndex = page
                                },
                                modifier = Modifier
                                    .width(
                                        this@BoxWithConstraints.maxWidth
                                    )
                                    .background(Color.Black)
                                    .fillMaxHeight()
                            )
                        }

                    }
                }
            }
        }

    }

}

@Preview
@Composable
fun IllustDetailScreenPreview() {
    val context = LocalContext.current
    val viewModel = remember {
        IllustViewModel(
            KtorPixivRecommendApi(
                appApiHttpClient(
                    hashInterceptor(),
                    tokenInterceptor(
                        AppRepository(
                            context.applicationContext.getSharedPreferences(AppRepository.AppStorage, Context.MODE_PRIVATE),
                            KtorPixivAuthApi(oauthClient(hashInterceptor()))
                        )
                    )
                )
            )
        )
    }

    LaunchedEffect(Unit) {
        viewModel.reloadIllusts()
    }

    IllustDetailScreen(viewModel)

}