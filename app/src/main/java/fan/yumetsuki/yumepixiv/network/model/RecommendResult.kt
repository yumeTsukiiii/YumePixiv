package fan.yumetsuki.yumepixiv.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecommendResult(
    @SerialName("illusts")
    val illusts: List<PixivIllust>,
    @SerialName("ranking_illusts")
    val rankingIllusts: List<RankingIllust>? = null,
    @SerialName("contest_exists")
    val contestExists: Boolean,
    @SerialName("privacy_policy")
    val privacyPolicy: PrivacyPolicy? = null,
    @SerialName("next_url")
    val nextUrl: String
) {

    @Serializable
    data class PrivacyPolicy(
        @SerialName("version")
        val version: String? = null,
        @SerialName("message")
        val message: String? = null,
        @SerialName("url")
        val url: String? = null
    )

}