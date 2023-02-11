package fan.yumetsuki.yumepixiv.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixivImageUrl(
    @SerialName("large")
    val large: String? = null,
    @SerialName("medium")
    val medium: String? = null,
    @SerialName("square_medium")
    val squareMedium: String? = null,
    @SerialName("original")
    val original: String? = null
)