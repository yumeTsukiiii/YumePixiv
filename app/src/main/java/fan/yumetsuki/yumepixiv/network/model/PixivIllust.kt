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
    val imageUrls: ImageUrl,
    @SerialName("caption")
    val caption: String,
    @SerialName("restrict")
    val restrict: Int,
    @SerialName("user")
    val user: User,
    @SerialName("tags")
    val tags: List<Tag>,
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
    val series: Series? = null,
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
    data class ImageUrl(
        @SerialName("large")
        val large: String? = null,
        @SerialName("medium")
        val medium: String? = null,
        @SerialName("square_medium")
        val squareMedium: String? = null,
        @SerialName("original")
        val original: String? = null
    )

    @Serializable
    data class User(
        @SerialName("id")
        val id: Long,
        @SerialName("name")
        val name: String,
        @SerialName("account")
        val account: String,
        @SerialName("profile_image_urls")
        val profileImageUrls: ImageUrl,
        @SerialName("is_followed")
        val isFollowed: Boolean
    )

    @Serializable
    data class Tag(
        @SerialName("name")
        val name: String,
        @SerialName("translated_name")
        val translatedName: String? = null
    )

    @Serializable
    data class Series(
        @SerialName("id")
        val id: Long,
        @SerialName("title")
        val title: String
    )

    @Serializable
    data class MetaSinglePage(
        @SerialName("original_image_url")
        val originalImageUrl: String? = null
    )

    @Serializable
    data class MetaPage(
        @SerialName("image_urls")
        val imageUrls: ImageUrl
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