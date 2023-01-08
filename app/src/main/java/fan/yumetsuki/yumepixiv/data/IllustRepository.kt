package fan.yumetsuki.yumepixiv.data

import fan.yumetsuki.yumepixiv.api.PixivRecommendApi
import fan.yumetsuki.yumepixiv.api.model.PixivIllust
import fan.yumetsuki.yumepixiv.data.model.Illust
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class IllustRepository constructor(
    private val pixivRecommendApi: PixivRecommendApi,
    private val coroutineScope: CoroutineScope
) {

    private val lock = Mutex()

    private var nextIllustUrl: String? = null

    private val _illusts = MutableSharedFlow<List<Illust>>()

    private val _rankingIllusts = MutableSharedFlow<List<Illust>>()

    val illusts = _illusts.asSharedFlow()

    val rankingIllust = _rankingIllusts.asSharedFlow()

    suspend fun refreshIllusts() {
        withContext(coroutineScope.coroutineContext + Dispatchers.IO) {
            pixivRecommendApi.getRecommendIllust().also { result ->
                _illusts.emit(result.illusts.toIllustModel())
                _rankingIllusts.emit(result.rankingIllusts?.toIllustModel() ?: emptyList())
            }
        }.also { result ->
            nextIllustUrl = result.nextUrl
        }
    }

    suspend fun nextPageIllust() {
        val nextIllustUrl = lock.withLock {
            this.nextIllustUrl
        }
        nextIllustUrl?.also { nextUrl ->
            withContext(coroutineScope.coroutineContext + Dispatchers.IO) {
                pixivRecommendApi.nextPageRecommendIllust(nextUrl).also { result ->
                    _illusts.emit(result.illusts.toIllustModel())
                }
            }.also { result ->
                lock.withLock {
                    this.nextIllustUrl = result.nextUrl
                }
            }
        }
    }

    private fun List<PixivIllust>.toIllustModel() = this.map { it.toIllustModel() }

    private fun PixivIllust.toIllustModel(): Illust {
        return Illust(
            id = id,
            title = title,
            coverPage = imageUrls.run {
                squareMedium ?: medium
            },
            user = user.run {
                Illust.User(
                    id = id,
                    name = name,
                    account = account,
                    avatar = profileImageUrls.run {
                        squareMedium ?: medium
                    },
                    isFollowed = isFollowed
                )
            },
            pageCount = pageCount,
            metaPages = metaPages.map { metaPage ->
                metaPage.imageUrls.run {
                    Illust.Image(
                        original = original,
                        large = large,
                        medium = medium,
                        squareMedium = squareMedium
                    )
                }
            },
            totalView = totalView,
            totalBookMarks = totalBookMarks,
            isBookMarked = isBookMarked,
        )
    }

}