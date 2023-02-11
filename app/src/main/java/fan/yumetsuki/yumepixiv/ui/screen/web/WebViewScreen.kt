package fan.yumetsuki.yumepixiv.ui.screen.web

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import fan.yumetsuki.yumepixiv.network.defaultHeader
import fan.yumetsuki.yumepixiv.ui.screen.Route
import io.ktor.http.*
import java.net.URLDecoder

const val URL = "url"
const val TOKEN = "token"
const val HOST = "host"

val webView = Route(
    route = "webView"
)

fun NavGraphBuilder.webViewScreen() {
    composable(
        "${webView.route}?${URL}={$URL}&${TOKEN}={$TOKEN}&${HOST}={$HOST}",
        arguments = listOf(
            navArgument(URL) {
                nullable = true
            },
            navArgument(TOKEN) {
                nullable = true
            },
            navArgument(HOST) {
                nullable = true
            }
        )
    ) { navBackStackEntry ->
        val url = navBackStackEntry.arguments?.getString(URL) ?: error("WebView url args is null")
        val token = navBackStackEntry.arguments?.getString(TOKEN)
        val host = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            null
        } else {
            navBackStackEntry.arguments?.getString(HOST) ?: Uri.parse(url).host
        }
        WebViewScreen(
            url = URLDecoder.decode(url, "UTF-8"),
            headers = buildMap {
                if (token != null) {
                    put(HttpHeaders.Authorization, "Bearer $token")
                }
                putAll(defaultHeader(null))
            }
        )
    }
}

fun NavController.navigateToWebView(
    url: String,
    token: String? = null,
    host: String? = null,
    options: NavOptions? = null
) {
    val arguments = listOf(
        URL to url,
        TOKEN to token,
        HOST to host
    ).filter { (_, value) ->
        value != null
    }.joinToString(separator = "&") { (key, value) ->
        "${key}=${value}"
    }
    navigate("${webView.route}?${arguments}", options)
}

fun NavController.navigateToWebView(
    url: String,
    token: String? = null,
    host: String? = null,
    builder: NavOptionsBuilder.() -> Unit
) {
    navigateToWebView(url, token, host, navOptions(builder))
}


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(
    url: String,
    headers: Map<String, String> = emptyMap()
) {

    println("WebUrl $url $headers")

    val webViewState = rememberWebViewState(
        url = url,
        additionalHttpHeaders = headers
    )

    WebView(
        // TODO 处理内部部分 js 点击事件，例如小说加载下一章节
        state = webViewState,
        onCreated = { webView ->
            webView.settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                loadsImagesAutomatically = true
            }
        },
        modifier = Modifier.fillMaxSize()
    )

}