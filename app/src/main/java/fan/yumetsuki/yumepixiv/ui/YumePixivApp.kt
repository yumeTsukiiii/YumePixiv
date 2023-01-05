package fan.yumetsuki.yumepixiv.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import fan.yumetsuki.yumepixiv.ui.screen.dynamicMessage
import fan.yumetsuki.yumepixiv.ui.screen.home


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YumePixivApp() {
    val appRoutes = listOf(home, dynamicMessage)
    val navController = rememberNavController()

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
                            Text(text = navPage.label)
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
            appRoutes.forEach { navPage ->
                navPage.content(this, navController)
            }
        }
    }

}