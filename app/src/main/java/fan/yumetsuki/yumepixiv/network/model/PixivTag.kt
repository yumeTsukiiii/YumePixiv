package fan.yumetsuki.yumepixiv.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixivTag(
    @SerialName("name")
    val name: String,
    @SerialName("translated_name")
    val translatedName: String? = null
)