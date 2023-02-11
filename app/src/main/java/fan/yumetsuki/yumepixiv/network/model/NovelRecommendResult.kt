package fan.yumetsuki.yumepixiv.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NovelRecommendResult(
    @SerialName("novels")
    val novels: List<PixivNovel>,
    @SerialName("ranking_novels")
    val rankingNovels: List<RankingNovel>? = null,
    @SerialName("privacy_policy")
    val privacyPolicy: PrivacyPolicy? = null,
    @SerialName("next_url")
    val nextUrl: String
)