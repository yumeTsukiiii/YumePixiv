package fan.yumetsuki.yumepixiv.data.model

class Novel(
    val id: Long,
    val title: String,
    val caption: String,
    /**
     * 插画推荐缩略图
     */
    val coverPage: String?,
    val user: UserModel,
    val pageCount: Int,
    val totalView: Int,
    val totalBookmarks: Int,
    val isBookMarked: Boolean,
    val createDate: String,
    val textLength: Int,
    val series: SeriesModel?,
    val tags: List<TagModel>,
)