package fan.yumetsuki.yumepixiv.ui.screen.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Publish
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import fan.yumetsuki.yumepixiv.ui.screen.Route
import fan.yumetsuki.yumepixiv.ui.screen.main.home.home
import fan.yumetsuki.yumepixiv.ui.screen.main.home.homeScreen
import fan.yumetsuki.yumepixiv.ui.screen.main.news.dynamicMessageScreen
import fan.yumetsuki.yumepixiv.ui.screen.main.news.news

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
    val label: String
)

private val drawGroups = listOf(
    DrawerGroup(
        label = "我的",
        items = listOf(
            DrawerItem(
                icon = Icons.Default.Home,
                label = "个人主页"
            ),
            DrawerItem(
                icon = Icons.Default.Favorite,
                label = "收藏"
            ),
            DrawerItem(
                icon = Icons.Default.Group,
                label = "关注"
            ),
            DrawerItem(
                icon = Icons.Default.Groups,
                label = "粉丝"
            )
        )
    ),
    DrawerGroup(
        label = "功能",
        items = listOf(
            DrawerItem(
                icon = Icons.Default.History,
                label = "浏览记录"
            ),
            DrawerItem(
                icon = Icons.Outlined.Publish,
                label = "投稿"
            )
        )
    ),
    DrawerGroup(
        label = "其他",
        items = listOf(
            DrawerItem(
                icon = Icons.Default.Info,
                label = "关于"
            ),
            DrawerItem(
                icon = Icons.Default.Settings,
                label = "设置"
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
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                drawGroups.forEach { drawerGroup ->
                    Text(text = drawerGroup.label)

//                    drawerGroup.items.forEach {
//                        NavigationDrawerItem(
//                            label = { /*TODO*/ },
//                            selected = ,
//                            onClick = { /*TODO*/ })
//                    }
                }
            }
        }
    ) {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    appRoutes.forEach { navPage ->
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination

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