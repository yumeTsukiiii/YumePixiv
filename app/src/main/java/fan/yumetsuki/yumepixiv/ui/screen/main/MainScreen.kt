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
        label = "??????",
        items = listOf(
            DrawerItem(
                icon = Icons.Default.Home,
                label = "????????????",
                route = ""
            ),
            DrawerItem(
                icon = Icons.Default.Favorite,
                label = "??????",
                route = ""
            ),
            DrawerItem(
                icon = Icons.Default.Group,
                label = "??????",
                route = ""
            ),
            DrawerItem(
                icon = Icons.Default.Groups,
                label = "??????",
                route = ""
            )
        )
    ),
    DrawerGroup(
        label = "??????",
        items = listOf(
            DrawerItem(
                icon = Icons.Default.History,
                label = "????????????",
                route = ""
            ),
            DrawerItem(
                icon = Icons.Outlined.Publish,
                label = "??????",
                route = ""
            )
        )
    ),
    DrawerGroup(
        label = "??????",
        items = listOf(
            DrawerItem(
                icon = Icons.Default.Info,
                label = "??????",
                route = ""
            ),
            DrawerItem(
                icon = Icons.Default.Settings,
                label = "??????",
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
    val userName = "???????????????"

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
                                    // ?????? popUpTo ??????????????????????????? pop ??? ??????
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        // ?????? saveState ??????????????????????????????????????????????????????????????????????????????????????????
                                        // ???????????? fragment ??????????????????
                                        saveState = true
                                    }
                                    // singleTop ???????????????????????????????????????????????????
                                    // ???????????????????????? popUpTo ????????? inclusive = true
                                    launchSingleTop = true
                                    // ???????????????????????????????????????????????????????????? saveState ??????????????????????????? fragment ??????????????????
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