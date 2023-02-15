package fan.yumetsuki.yumepixiv.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixivBookmarksIllusts(
    @SerialName("next_url")
    val nextUrl: String? = null,
    @SerialName("novels")
    val novels: List<PixivIllust>
)