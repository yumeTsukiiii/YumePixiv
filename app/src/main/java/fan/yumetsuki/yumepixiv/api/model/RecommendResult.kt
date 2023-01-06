package fan.yumetsuki.yumepixiv.api.model

data class RecommendResult(
    val illusts: List<PixivIllust>,
    val rankingIllusts: List<RankingIllust>?,
    val contestExists: Boolean,
    val privacyPolicy: PrivacyPolicy,
    val nextUrl: String
) {

    data class PrivacyPolicy(
        val version: String,
        val message: String,
        val url: String
    )

}