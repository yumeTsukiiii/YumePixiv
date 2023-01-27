package fan.yumetsuki.yumepixiv.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IllustRecommendResult(
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
)