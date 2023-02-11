package fan.yumetsuki.yumepixiv.data

import fan.yumetsuki.yumepixiv.network.PixivRecommendApi
import fan.yumetsuki.yumepixiv.data.model.Novel
import fan.yumetsuki.yumepixiv.data.model.SeriesModel
import fan.yumetsuki.yumepixiv.data.model.TagModel
import fan.yumetsuki.yumepixiv.data.model.UserModel
import fan.yumetsuki.yumepixiv.network.model.PixivNovel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class NovelRepository constructor(
    private val pixivRecommendApi: PixivRecommendApi,
    private val coroutineScope: CoroutineScope
) {

    private val lock = Mutex()

    private var nextIllustUrl: String? = null

    private val _pagedNovels = MutableStateFlow<List<List<Novel>>>(emptyList())
    private val _pagedRankingNovels = MutableStateFlow<List<List<Novel>>>(emptyList())

    val novels = _pagedNovels.map {
        it.flatten()
    }.stateIn(coroutineScope, SharingStarted.Eagerly, emptyList())

    val rankingNovels = _pagedRankingNovels.map {
        it.flatten()
    }.stateIn(coroutineScope, SharingStarted.Eagerly, emptyList())

    suspend fun refreshNovels() {
        withContext(coroutineScope.coroutineContext + Dispatchers.IO) {
            pixivRecommendApi.getRecommendNovels().also { result ->
                _pagedNovels.update {
                    buildList {
                        add(result.novels.toNovelModel())
                    }
                }
                _pagedRankingNovels.update {
                    buildList {
                        add(result.rankingNovels?.toNovelModel() ?: emptyList())
                    }
                }
            }
        }.also { result ->
            nextIllustUrl = result.nextUrl
        }
    }

    suspend fun addNovelBookMark(novel: Novel) {
        return withContext(coroutineScope.coroutineContext + Dispatchers.IO) {
            pixivRecommendApi.addNovelBookmark(novel.id, BookmarkRestrictPublic)
        }
    }

    suspend fun deleteNovelBookMark(novel: Novel) {
        return withContext(coroutineScope.coroutineContext + Dispatchers.IO) {
            pixivRecommendApi.deleteNovelBookmark(novel.id)
        }
    }

    suspend fun nextPageIllust() {
        val nextIllustUrl = lock.withLock {
            this.nextIllustUrl
        }
        nextIllustUrl?.also { nextUrl ->
            withContext(coroutineScope.coroutineContext + Dispatchers.IO) {
                pixivRecommendApi.nextPageNovels(nextUrl).also { result ->
                    _pagedNovels.update { oldIllusts ->
                        oldIllusts.toMutableList().apply {
                            add(result.novels.toNovelModel())
                        }
                    }
                }
            }.also { result ->
                lock.withLock {
                    this.nextIllustUrl = result.nextUrl
                }
            }
        }
    }

    private fun List<PixivNovel>.toNovelModel() = this.map { it.toNovelModel() }

    private fun PixivNovel.toNovelModel(): Novel {
        return Novel(
            id = id,
            title = title,
            caption = caption,
            createDate = createDate,
            coverPage = imageUrls.run {
               medium ?: squareMedium ?: large ?: original
            },
            user = user.run {
                UserModel(
                    id = id,
                    name = name,
                    account = account,
                    avatar = profileImageUrls.run {
                        squareMedium ?: medium ?: large ?: original
                    },
                    isFollowed = isFollowed
                )
            },
            pageCount = pageCount,
            totalView = totalView,
            totalBookmarks = totalBookMarks,
            isBookMarked = isBookMarked,
            textLength = textLength,
            series = series?.takeIf {
                it.id != null && it.title != null
            }?.let {
                SeriesModel(
                    id = series.id!!,
                    title = series.title!!
                )
            },
            tags = tags.map { TagModel(it.name, it.translatedName) }
        )
    }

    companion object {

        const val BookmarkRestrictPublic: String = "public"

        const val BookmarkRestrictPrivate: String = "private"

    }
}