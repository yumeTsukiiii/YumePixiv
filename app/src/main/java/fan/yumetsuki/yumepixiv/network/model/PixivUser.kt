package fan.yumetsuki.yumepixiv.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixivUser(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("account")
    val account: String,
    @SerialName("profile_image_urls")
    val profileImageUrls: PixivImageUrl,
    @SerialName("is_followed")
    val isFollowed: Boolean
)
