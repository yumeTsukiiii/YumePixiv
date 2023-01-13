package fan.yumetsuki.yumepixiv.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest

@Composable
fun pixivImageRequestBuilder(imageUrl: String): ImageRequest.Builder {
    return ImageRequest.Builder(LocalContext.current)
        .data(imageUrl)
        .addHeader("Referer", "https://app-api.pixiv.net/")
        // TODO 不要每次计算，给一个特定的 host 全局配置
        .addHeader("Host", imageUrl.split("/")[2])
}