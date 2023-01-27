package fan.yumetsuki.yumepixiv.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RelatedResult(
    @SerialName("illusts")
    val illusts: List<PixivIllust>,
    @SerialName("next_url")
    val nextUrl: String
)