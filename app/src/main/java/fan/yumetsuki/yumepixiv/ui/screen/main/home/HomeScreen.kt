package fan.yumetsuki.yumepixiv.ui.screen.main.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import fan.yumetsuki.yumepixiv.ui.screen.Route
import fan.yumetsuki.yumepixiv.viewmodels.IllustViewModel
import fan.yumetsuki.yumepixiv.viewmodels.MangaViewModel

val home = Route(
    route = "home",
    label = "首页",
    icon = Icons.Default.Home,
)

fun NavGraphBuilder.homeScreen(illustViewModel: IllustViewModel? = null) {
    composable(home.route) {
        Home(
            illustViewModel = illustViewModel ?: hiltViewModel()
        )
    }
}

class TabItem(
    val label: String,
    val content: @Composable () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    illustViewModel: IllustViewModel = hiltViewModel(),
    mangaViewModel: MangaViewModel = hiltViewModel()
) {

    val tabs = remember {
        listOf(
            TabItem("插画") { IllustScreen(Modifier.fillMaxSize(), illustViewModel) },
            TabItem("漫画") { MangaScreen(Modifier.fillMaxSize(), mangaViewModel) },
            TabItem("小说") { NovelScreen() }
        )
    }

    // navigation 切换需要使用 saveable
    var selectedTabIndex by rememberSaveable {
        mutableStateOf(0)
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val saveableStateHolder = rememberSaveableStateHolder()

//    val pagerState = rememberPagerState()
//    val selectedPage by remember {
//        derivedStateOf {
//            pagerState.currentPage
//        }
//    }
//
//    LaunchedEffect(selectedTabIndex) {
//        if (selectedTabIndex != selectedPage) {
//            pagerState.animateScrollToPage(selectedTabIndex)
//        }
//    }
//
//    LaunchedEffect(selectedPage) {
//        selectedTabIndex = selectedPage
//    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = null)
                    }
                },
                title = {
                    Text(text = "主页")
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->

        Column(modifier = Modifier.padding(paddingValues)) {
            TabRow(
                selectedTabIndex,
                modifier = Modifier.zIndex(1f)
            ) {
                tabs.forEachIndexed { index, route ->
                    Tab(
                        selected = index == selectedTabIndex,
                        onClick = {
                            selectedTabIndex = index
                        },
                        text = {
                            Text(text = route.label, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    )
                }
            }
            Box(modifier = Modifier.weight(1.0f)) {
                // FIXME Pager 暂无法和 Material3 Tab Indicator 联动，TabPosition API 为 internal
                //       而且会和 Tab 有跳动的 Bug，还是和嵌套滚动容器有关，官方 TabBehaviour 的 Bug
//                HorizontalPager(
//                    count = tabs.size,
//                    state = pagerState
//                ) { page ->
//                    tabs[page].content()
//                }
                tabs.forEachIndexed { index, tabItem ->
                    this@Column.AnimatedVisibility(
                        visible = selectedTabIndex == index,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        // AnimatedVisibility 的 content invisible 会被 dispose 掉
                        // 下次重组时，需要利用 saveable 恢复状态
                        saveableStateHolder.SaveableStateProvider("YumePixivHomeTab-$index") {
                            tabItem.content()
                        }
                    }
                }
            }
        }

    }

}