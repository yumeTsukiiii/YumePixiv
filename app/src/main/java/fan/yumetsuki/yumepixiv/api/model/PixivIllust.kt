package fan.yumetsuki.yumepixiv.api.model

data class PixivIllust(
    val id: Long,
    val title: String,
    /**
     * TODO 类型 illust、manga、novel，最终确认下换成枚举 or StringRef
     */
    val type: String,
    val imageUrls: ImageUrl,
    val caption: String,
    val restrict: Int,
    val user: User,
    val tags: List<Tag>,
    val tools: List<String>,
    /**
     * TODO 看 Ktor 场景直接换成 Date？
     */
    val createDate: String,
    val pageCount: Int,
    val width: Int,
    val height: Int,
    val sanityLevel: Int,
    val xRestrict: Int,
    /**
     * TODO 确认 series 的类型
     * */
    // val series
    val metaSinglePage: MetaSinglePage,
    val metaPages: List<MetaPage>,
    val totalView: Int,
    val totalBookMarks: Int,
    val isBookMarked: Boolean,
    val visible: Boolean,
    val isMuted: Boolean,
    /**
     * TODO 看到底有哪些，是否需要换成枚举
     */
    val illustAiType: Int,
    /**
     * TODO 看到底有哪些，是否需要换成枚举
     */
    val illustBookStyle: Int
) {

    data class ImageUrl(
        val large: String?,
        val medium: String?,
        val squareMedium: String?,
        val original: String?
    )

    data class User(
        val id: Long,
        val name: String,
        val account: String,
        val profileImageUrls: ImageUrl,
        val isFollowed: Boolean
    )

    data class Tag(
        val name: String,
        val translatedName: String
    )

    data class MetaSinglePage(
        val originalImageUrl: String?
    )

    data class MetaPage(
        val imageUrls: ImageUrl
    )
}

typealias RankingIllust = PixivIllust