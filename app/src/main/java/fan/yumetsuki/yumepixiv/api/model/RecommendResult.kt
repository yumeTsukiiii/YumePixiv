package fan.yumetsuki.yumepixiv.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecommendResult(
    @SerialName("illusts")
    val illusts: List<PixivIllust>,
    @SerialName("ranking_illusts")
    val rankingIllusts: List<PixivIllust>? = null,
    @SerialName("contest_exists")
    val contestExists: Boolean,
    @SerialName("privacy_policy")
    val privacyPolicy: PrivacyPolicy,
    @SerialName("next_url")
    val nextUrl: String
) {

    @Serializable
    data class PrivacyPolicy(
        val version: String,
        val message: String,
        val url: String
    )

}