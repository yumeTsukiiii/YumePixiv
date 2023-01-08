package fan.yumetsuki.yumepixiv.data.model

data class Illust(
    val id: Long,
    val title: String,
    /**
     * 插画推荐缩略图
     */
    val coverPage: String?,
    val user: User,
    val pageCount: Int,
    val metaPages: List<Image>,
    val totalView: Int,
    val totalBookMarks: Int,
    val isBookMarked: Boolean,
) {

    data class User(
        val id: Long,
        val name: String,
        val account: String,
        val avatar: String?,
        val isFollowed: Boolean
    )

    data class Tag(
        val name: String,
        val translatedName: String
    )

    data class Image(
        val original: String? = null,
        val large: String? = null,
        val medium: String? = null,
        val squareMedium: String? = null
    ) {

        val url: String?
            get() = medium ?: large ?: squareMedium ?: original

    }
}