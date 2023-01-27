package fan.yumetsuki.yumepixiv.ui.screen.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import fan.yumetsuki.yumepixiv.ui.components.YumePixivTip


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun IllustDetailScreen(
    onCloseClick: () -> Unit,
    initialPage: Int,
    illustDetails: List<IllustDetail>
) {

    var currentVisibleImageIndex by remember {
        mutableStateOf(initialPage)
    }
    val pagerState = rememberPagerState(currentVisibleImageIndex)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                     IconButton(onClick = onCloseClick) {
                         Icon(imageVector = Icons.Default.Close, contentDescription = null)
                     }
                },
                title = {
                    Text(
                        text = "${currentVisibleImageIndex + 1} / ${illustDetails[pagerState.currentPage].images.size}",
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
            if (illustDetails.isEmpty()) {
                YumePixivTip(
                    text = "芜湖...好像好像什么东西也没有？！",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 96.dp)
                )
            } else {
                HorizontalPager(
                    state = pagerState,
                    count = illustDetails.size,
                    modifier = Modifier
                        .fillMaxSize()
                ) { page ->

                    illustDetails[page].also { illustDetail ->
                        IllustDetail(
                            illustDetail = illustDetail,
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