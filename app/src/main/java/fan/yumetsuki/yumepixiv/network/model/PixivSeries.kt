package fan.yumetsuki.yumepixiv.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixivSeries(
    @SerialName("id")
    val id: Long? = null,
    @SerialName("title")
    val title: String? = null
)