package fan.yumetsuki.yumepixiv.data

import fan.yumetsuki.yumepixiv.api.PixivRecommendApi
import fan.yumetsuki.yumepixiv.api.model.PixivIllust
import fan.yumetsuki.yumepixiv.data.model.Illust
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IllustRepository(
    private val pixivRecommendApi: PixivRecommendApi,
    private val coroutineScope: CoroutineScope
) {

    private val _illusts = MutableSharedFlow<List<Illust>>()

    val illusts: SharedFlow<List<Illust>> = _illusts.asSharedFlow()

    private var nextIllustUrl: String? = null

    suspend fun refreshIllust() {
        withContext(coroutineScope.coroutineContext + Dispatchers.IO) {
            val result = pixivRecommendApi.getRecommendIllust()
            nextIllustUrl = result.nextUrl
            _illusts.emit(result.illusts.map { pixivIllustToModel(it) })
        }
    }

    fun appendNextPageIllust() {

    }

    private fun pixivIllustToModel(illust: PixivIllust): Illust {
        return Illust(
            id = illust.id,
            title = illust.title,
            coverPage = illust.imageUrls.run {
                squareMedium ?: medium ?: TODO("默认图片")
            },
            user = illust.user.run {
                Illust.User(
                    id = id,
                    name = name,
                    account = account,
                    avatar = profileImageUrls.run {
                        squareMedium ?: medium ?: TODO("默认图片")
                    },
                    isFollowed = isFollowed
                )
            },
            pageCount = illust.pageCount,
            metaPages = illust.metaPages.map { metaPage ->
                metaPage.imageUrls.run {
                    Illust.Image(
                        original = original,
                        large = large,
                        medium = medium,
                        squareMedium = squareMedium
                    )
                }
            },
            totalView = illust.totalView,
            totalBookMarks = illust.totalBookMarks,
            isBookMarked = illust.isBookMarked
        )
    }

}