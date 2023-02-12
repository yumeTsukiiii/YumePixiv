package fan.yumetsuki.yumepixiv.viewmodels

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.JavascriptInterface
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.google.accompanist.web.WebViewState
import com.google.accompanist.web.rememberWebViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import fan.yumetsuki.yumepixiv.data.AppRepository
import fan.yumetsuki.yumepixiv.network.PixivHosts
import fan.yumetsuki.yumepixiv.network.defaultHeader
import fan.yumetsuki.yumepixiv.ui.screen.novelviewer.novelViewer
import fan.yumetsuki.yumepixiv.utils.toColorHexString
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.internal.toHexString
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltViewModel
class NovelViewerViewModel @Inject constructor(
    val appRepository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        UiState(0, 0)
    )

    private val jsonSerializer = Json {
        encodeDefaults = true
        isLenient = true
        allowSpecialFloatingPointValues = true
        allowStructuredMapKeys = true
        prettyPrint = false
        useArrayPolymorphism = false
        ignoreUnknownKeys = true
    }

    val uiState = _uiState.asStateFlow()

    @Composable
    fun rememberNovelWebViewState(novelId: Long): WebViewState {
        return rememberWebViewState(
            url = """
                    https://${PixivHosts.AppApi}/webview/v2/novel
                    ?id=${novelId}
                    &font=default&font_size=16.0px
                    &line_height=1.75
                    &color=${MaterialTheme.colorScheme.onSurface.toColorHexString()}
                    &background_color=${MaterialTheme.colorScheme.background.toColorHexString()}
                    &margin_top=56px
                    &margin_bottom=53px
                    &theme=${if (isSystemInDarkTheme()) "dart" else "light"}
                    &use_block=true
                    &viewer_version=20221031_ai
                """.trimIndent(),
            additionalHttpHeaders = buildMap {
                put(HttpHeaders.Authorization, "Bearer ${appRepository.accessToken}")
                putAll(defaultHeader(null))
            }
        )
    }

    @Composable
    fun rememberNovelWebViewJsListener(rootNavController: NavController, context: Context): NovelWebViewJsListener {
        return remember(rootNavController, context) {
            NovelWebViewJsListener(rootNavController, WeakReference(context))
        }
    }

    inner class NovelWebViewJsListener(
        private val rootNavController: NavController,
        private val contextRef: WeakReference<Context>
    ) {

        @JavascriptInterface
        fun postMessage(message: String?) {
            if (message.isNullOrEmpty()) {
                return
            }
            val novelViewerMessage = jsonSerializer.decodeFromString<NovelViewerMessage>(message)
            viewModelScope.launch(Dispatchers.Main) {
                if (novelViewerMessage.ready != null) {
                    _uiState.update { oldState ->
                        oldState.copy(
                            currentPage = if (novelViewerMessage.ready.totalPageCount == 0) oldState.currentPage else novelViewerMessage.ready.totalPageCount,
                            totalPageCount = novelViewerMessage.ready.totalPageCount
                        )
                    }
                }
                if (novelViewerMessage.openContent != null) {
                    if (novelViewerMessage.openContent.inApp) {
                        // TODO WebView 暂不支持 navigation restore state
                        try {
                            // TODO 还需支持 tag 搜索 /  bookmark detail / user 等其他点击跳转 App 内页面事件，否则会 crash
                            rootNavController.navigate(
                                Uri.parse(novelViewerMessage.openContent.uri)
                            )
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        }
                    } else {
                        contextRef.get()?.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse(novelViewerMessage.openContent.uri))
                        )
                    }
                }
            }
        }

    }

    data class UiState(
        val currentPage: Int,
        val totalPageCount: Int
    )

}

@Serializable
data class NovelViewerMessage(
    // TODO 处理很多别的类型
    val openContent: Content? = null,
    val scroll: ScrollInfo? = null,
    val ready: NovelInfo? = null
) {

    @Serializable
    data class Content(
        val uri: String,
        val inApp: Boolean = true
    )

    @Serializable
    data class ScrollInfo(
        val page: Int,
        val state: String
    )

    @Serializable
    data class NovelInfo(
        val totalPageCount: Int
    )

}