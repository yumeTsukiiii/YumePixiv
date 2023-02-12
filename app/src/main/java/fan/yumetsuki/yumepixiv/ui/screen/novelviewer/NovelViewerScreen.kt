package fan.yumetsuki.yumepixiv.ui.screen.novelviewer

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.web.WebView
import fan.yumetsuki.yumepixiv.ui.screen.Route
import fan.yumetsuki.yumepixiv.viewmodels.NovelViewerViewModel
import io.ktor.http.*

const val NOVEL_ID = "novel_id"
const val ID = "id"

val novelViewer = Route(
    route = "novelViewer"
)

fun NavGraphBuilder.novelViewScreen(
    rootNavController: NavController? = null
) {
    composable(
        "${novelViewer.route}?${NOVEL_ID}={$NOVEL_ID}",
        arguments = listOf(
            navArgument(NOVEL_ID) {
                defaultValue = -1
                nullable = false
                type = NavType.LongType
            },
        ),
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "https://www.pixiv.net/novel/show.php?${ID}={$NOVEL_ID}"
            }
        )
    ) { navBackStackEntry ->

        NovelViewerScreen(
            novelId = navBackStackEntry.arguments?.getLong(NOVEL_ID, -1)?.takeIf { it >= 0 } ?: error("novel id is null"),
            rootNavController = rootNavController ?: rememberNavController()
        )
    }
}

fun NavController.navigateToNovelViewer(
    novelId: Long,
    options: NavOptions? = null
) {
    val arguments = listOf<Pair<String, Any?>>(
        NOVEL_ID to novelId,
    ).filter { (_, value) ->
        value != null
    }.joinToString(separator = "&") { (key, value) ->
        "${key}=${value}"
    }
    navigate("${novelViewer.route}?${arguments}", options)
}

@Suppress("unused")
fun NavController.navigateToNovelViewer(
    novelId: Long,
    builder: NavOptionsBuilder.() -> Unit
) {
    navigateToNovelViewer(novelId, navOptions(builder))
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NovelViewerScreen(
    novelId: Long,
    rootNavController: NavController = rememberNavController(),
    context: Context = LocalContext.current,
    viewModel: NovelViewerViewModel = hiltViewModel()
) {

    val screenState by viewModel.uiState.collectAsState()
    val webViewState = viewModel.rememberNovelWebViewState(novelId = novelId)
    val novelWebViewJsListener = viewModel.rememberNovelWebViewJsListener(
        rootNavController = rootNavController,
        context = context
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        rootNavController.navigateUp()
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                title = {
                    Text(
                        text = "${screenState.currentPage} / ${screenState.totalPageCount}",
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
                    scrolledContainerColor = Color.Transparent
                )
            )
        }
    ) {
        WebView(
            state = webViewState,
            onCreated = { webView ->
                webView.settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    loadsImagesAutomatically = true
                }
                webView.addJavascriptInterface(novelWebViewJsListener, "android")
            },
            modifier = Modifier
                .fillMaxSize()
        )
    }

}