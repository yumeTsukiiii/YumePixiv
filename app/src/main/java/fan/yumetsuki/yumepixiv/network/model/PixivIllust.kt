package fan.yumetsuki.yumepixiv.network.model

import androidx.annotation.StringDef
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixivIllust(
    @SerialName("id")
    val id: Long,
    @SerialName("title")
    val title: String,
    @SerialName("type")
    @IllustType
    val type: String,
    @SerialName("image_urls")
    val imageUrls: PixivImageUrl,
    @SerialName("caption")
    val caption: String,
    @SerialName("restrict")
    val restrict: Int,
    @SerialName("user")
    val user: PixivUser,
    @SerialName("tags")
    val tags: List<PixivTag>,
    @SerialName("tools")
    val tools: List<String>,
    @SerialName("create_date")
    val createDate: String,
    @SerialName("page_count")
    val pageCount: Int,
    @SerialName("width")
    val width: Int,
    @SerialName("height")
    val height: Int,
    @SerialName("sanity_level")
    val sanityLevel: Int,
    @SerialName("x_restrict")
    val xRestrict: Int,
    @SerialName("series")
    val series: PixivSeries? = null,
    @SerialName("meta_single_page")
    val metaSinglePage: MetaSinglePage,
    @SerialName("meta_pages")
    val metaPages: List<MetaPage>,
    @SerialName("total_view")
    val totalView: Int,
    @SerialName("total_bookmarks")
    val totalBookMarks: Int,
    @SerialName("is_bookmarked")
    val isBookMarked: Boolean,
    @SerialName("visible")
    val visible: Boolean,
    @SerialName("is_muted")
    val isMuted: Boolean,
    @SerialName("illust_ai_type")
    val illustAiType: Int,
    @SerialName("illust_book_style")
    val illustBookStyle: Int
) {

    @Serializable
    data class MetaSinglePage(
        @SerialName("original_image_url")
        val originalImageUrl: String? = null
    )

    @Serializable
    data class MetaPage(
        @SerialName("image_urls")
        val imageUrls: PixivImageUrl
    )
}

@StringDef(IllustType.Illust, IllustType.Manga)
annotation class IllustType {

    companion object {
        const val Illust = "illust"
        const val Manga = "manga"
    }

}

typealias RankingIllust = PixivIllust