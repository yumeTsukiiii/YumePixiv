package fan.yumetsuki.yumepixiv.data.model

data class Illust(
    val id: Long,
    val title: String,
    val caption: String,
    /**
     * 插画推荐缩略图
     */
    val coverPage: String?,
    val user: UserModel,
    val pageCount: Int,
    val metaPages: List<ImageModel>,
    val totalView: Int,
    val totalBookmarks: Int,
    val isBookMarked: Boolean,
    val createDate: String,
    val tags: List<TagModel>,
    val width: Int,
    val height: Int
)