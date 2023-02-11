package fan.yumetsuki.yumepixiv.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixivNovel(
    @SerialName("id")
    val id: Long,
    @SerialName("title")
    val title: String,
    @SerialName("caption")
    val caption: String,
    @SerialName("restrict")
    val restrict: Int,
    @SerialName("x_restrict")
    val xRestrict: Int,
    @SerialName("is_original")
    val isOriginal: Boolean,
    @SerialName("image_urls")
    val imageUrls: PixivImageUrl,
    @SerialName("create_date")
    val createDate: String,
    @SerialName("tags")
    val tags: List<PixivTag>,
    @SerialName("page_count")
    val pageCount: Int,
    @SerialName("text_length")
    val textLength: Int,
    @SerialName("user")
    val user: PixivUser,
    @SerialName("series")
    val series: PixivSeries? = null,
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
    @SerialName("total_comments")
    val totalComments: Int,
    @SerialName("is_mypixiv_only")
    val isMyPixivOnly: Boolean = false,
    @SerialName("is_x_restricted")
    val isXRestricted: Boolean = false,
    @SerialName("novel_ai_type")
    val novelAiType: Int
)

typealias RankingNovel = PixivNovel