package fan.yumetsuki.yumepixiv.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrivacyPolicy(
    @SerialName("version")
    val version: String? = null,
    @SerialName("message")
    val message: String? = null,
    @SerialName("url")
    val url: String? = null
)