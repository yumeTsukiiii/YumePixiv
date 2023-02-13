package fan.yumetsuki.yumepixiv.ui.screen.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Publish
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import fan.yumetsuki.yumepixiv.ui.components.NavigationDrawerDivider
import fan.yumetsuki.yumepixiv.ui.components.NavigationDrawerHeader
import fan.yumetsuki.yumepixiv.ui.components.NavigationDrawerItemHeadline
import fan.yumetsuki.yumepixiv.ui.screen.Route
import fan.yumetsuki.yumepixiv.ui.screen.main.home.home
import fan.yumetsuki.yumepixiv.ui.screen.main.home.homeScreen
import fan.yumetsuki.yumepixiv.ui.screen.main.news.dynamicMessageScreen
import fan.yumetsuki.yumepixiv.ui.screen.main.news.news
import fan.yumetsuki.yumepixiv.utils.pixivImageRequestBuilder
import kotlinx.coroutines.launch

val main = Route(
    route = "main"
)

fun NavGraphBuilder.mainScreen(
    navController: NavHostController? = null
) {
    composable(main.route) {
        MainScreen(navController ?: rememberNavController())
    }
}

@Suppress("unused")
fun NavController.navigateToMain(navOptions: NavOptions? = null) {
    navigate(main.route, navOptions)
}

fun NavController.navigateToMain(builder: NavOptionsBuilder.() -> Unit) {
    navigate(main.route, builder)
}

data class DrawerGroup(
    val label: String,
    val items: List<DrawerItem>
)

data class DrawerItem(
    val icon: ImageVector,
    val label: String,
    val route: String,
)

private val drawGroups = listOf(
    DrawerGroup(
        label = "我的",
        items = listOf(
            DrawerItem(
                icon = Icons.Default.Home,
                label = "个人主页",
                route = ""
            ),
            DrawerItem(
                icon = Icons.Default.Favorite,
                label = "收藏",
                route = ""
            ),
            DrawerItem(
                icon = Icons.Default.Group,
                label = "关注",
                route = ""
            ),
            DrawerItem(
                icon = Icons.Default.Groups,
                label = "粉丝",
                route = ""
            )
        )
    ),
    DrawerGroup(
        label = "功能",
        items = listOf(
            DrawerItem(
                icon = Icons.Default.History,
                label = "浏览记录",
                route = ""
            ),
            DrawerItem(
                icon = Icons.Outlined.Publish,
                label = "投稿",
                route = ""
            )
        )
    ),
    DrawerGroup(
        label = "其他",
        items = listOf(
            DrawerItem(
                icon = Icons.Default.Info,
                label = "关于",
                route = ""
            ),
            DrawerItem(
                icon = Icons.Default.Settings,
                label = "设置",
                route = ""
            )
        )
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    rootNavController: NavHostController = rememberNavController()
) {
    val appRoutes = listOf(home, news)
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val userAvatar = "https://tse4-mm.cn.bing.net/th/id/OIP-C.P5Y9Ph3AUf7NSr9GzYDHjAHaEo?w=280&h=180&c=7&r=0&o=5&dpr=2&pid=1.7"
    val userName = "二阶堂梦月"

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerHeader(
                    leadingContent = {
                        AsyncImage(
                            model = pixivImageRequestBuilder(imageUrl = userAvatar).build(),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(56.dp),
                            contentDescription = null
                        )
                    },
                    headlineText = {
                        Text(text = userName)
                    }
                )
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    drawGroups.forEachIndexed { index, drawerGroup ->
                        if (index != 0) {
                            NavigationDrawerDivider()
                        }
                        NavigationDrawerItemHeadline {
                            Text(text = drawerGroup.label)
                        }
                        drawerGroup.items.forEach { drawerItem ->
                            NavigationDrawerItem(
                                icon = {
                                    Icon(imageVector = drawerItem.icon, contentDescription = null)
                                },
                                label = {
                                    Text(text = drawerItem.label)
                                },
                                selected = currentDestination?.route == drawerItem.route,
                                onClick = {
                                    if (drawerItem.route.isNotEmpty()) {
                                        navController.navigate(drawerItem.route)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = null)
                        }
                    },
                    title = {
                        Text(
                            text = appRoutes.find { navPage ->
                                currentDestination?.hierarchy?.any { it.route == navPage.route } == true
                            }?.label ?: "unknown"
                        )
                    },
                    actions = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null)
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            bottomBar = {
                NavigationBar {
                    appRoutes.forEach { navPage ->
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any { it.route == navPage.route } == true,
                            onClick = {
                                navController.navigate(navPage.route) {
                                    // 使用 popUpTo 保证在返回键始终会 pop 到 首页
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        // 使用 saveState 保证在返回键时，将当前页面的状态保留，下次再跳转过去可以恢复
                                        // 类似两个 fragment 不销毁的操作
                                        saveState = true
                                    }
                                    // singleTop 模式，保证一个页面只会存在一个实例
                                    // 这里同等于可以在 popUpTo 里设置 inclusive = true
                                    launchSingleTop = true
                                    // 跳转到该页面时，尝试恢复到之间的状态，和 saveState 结合使用，类似两个 fragment 不销毁的操作
                                    restoreState = true
                                }
                            },
                            label = {
                                Text(text = navPage.label ?: "unknown")
                            },
                            icon = {
                                Icon(imageVector = navPage.icon ?: Icons.Default.Home, contentDescription = null)
                            }
                        )
                    }
                }
            }
        ) {
            NavHost(navController = navController, startDestination = home.route, modifier = Modifier.padding(it)) {
                homeScreen(rootNavController = rootNavController)
                dynamicMessageScreen()
            }
        }
    }
}