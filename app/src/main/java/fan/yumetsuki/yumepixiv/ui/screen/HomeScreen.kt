package fan.yumetsuki.yumepixiv.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import fan.yumetsuki.yumepixiv.ui.Route

val home = Route(
    route = "home",
    label = "首页",
    icon = Icons.Default.Home,
    content = { homeScreen() }
)

fun NavGraphBuilder.homeScreen() {
    composable(home.route) {
        Home()
    }
}

class TabItem(
    val label: String,
    val content: @Composable () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home() {

    val tabs = listOf(
        TabItem("插画") { IllustScreen(Modifier.fillMaxHeight()) },
        TabItem("漫画") { MangaScreen() },
        TabItem("小说") { NovelScreen() }
    )

    // navigation 切换需要使用 saveable
    var selectedTabIndex by rememberSaveable {
        mutableStateOf(0)
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

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
            TabRow(selectedTabIndex) {
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
                tabs.forEachIndexed { index, tabItem ->
                    this@Column.AnimatedVisibility(
                        visible = selectedTabIndex == index,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        tabItem.content()
                    }
                }
            }
        }

    }

}