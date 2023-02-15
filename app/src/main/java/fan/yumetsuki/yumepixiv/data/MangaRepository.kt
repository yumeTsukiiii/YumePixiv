package fan.yumetsuki.yumepixiv.data

import fan.yumetsuki.yumepixiv.network.PixivAppApi
import fan.yumetsuki.yumepixiv.network.model.PixivIllust
import fan.yumetsuki.yumepixiv.data.model.Illust
import fan.yumetsuki.yumepixiv.data.model.ImageModel
import fan.yumetsuki.yumepixiv.data.model.TagModel
import fan.yumetsuki.yumepixiv.data.model.UserModel
import fan.yumetsuki.yumepixiv.network.model.IllustType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class MangaRepository constructor(
    private val pixivAppApi: PixivAppApi,
    private val coroutineScope: CoroutineScope
) {

    private val lock = Mutex()

    private var nextIllustUrl: String? = null

    private val _pagedIllusts = MutableStateFlow<List<List<Illust>>>(emptyList())
    private val _pagedRankingIllusts = MutableStateFlow<List<List<Illust>>>(emptyList())

    val illusts = _pagedIllusts.map {
        it.flatten()
    }.stateIn(coroutineScope, SharingStarted.Eagerly, emptyList())

    val rankingIllust = _pagedRankingIllusts.map {
        it.flatten()
    }.stateIn(coroutineScope, SharingStarted.Eagerly, emptyList())

    suspend fun refreshIllusts() {
        withContext(coroutineScope.coroutineContext + Dispatchers.IO) {
            pixivAppApi.getRecommendMangas().also { result ->
                _pagedIllusts.update {
                    buildList {
                        add(result.illusts.toIllustModel())
                    }
                }
                _pagedRankingIllusts.update {
                    buildList {
                        add(result.rankingIllusts?.toIllustModel() ?: emptyList())
                    }
                }
            }
        }.also { result ->
            nextIllustUrl = result.nextUrl
        }
    }

    suspend fun addIllustBookMark(illust: Illust) {
        return withContext(coroutineScope.coroutineContext + Dispatchers.IO) {
            pixivAppApi.addIllustBookMark(illust.id, BookmarkRestrictPublic)
        }
    }

    suspend fun deleteIllustBookMark(illust: Illust) {
        return withContext(coroutineScope.coroutineContext + Dispatchers.IO) {
            pixivAppApi.deleteIllustBookMark(illust.id)
        }
    }

    suspend fun relatedIllusts(illust: Illust): List<Illust> {
        return withContext(coroutineScope.coroutineContext + Dispatchers.IO) {
            pixivAppApi.relatedIllusts(illust.id).illusts.toIllustModel()
        }
    }

    suspend fun nextPageIllust() {
        val nextIllustUrl = lock.withLock {
            this.nextIllustUrl
        }
        nextIllustUrl?.also { nextUrl ->
            withContext(coroutineScope.coroutineContext + Dispatchers.IO) {
                pixivAppApi.nextPageMangaIllusts(nextUrl).also { result ->
                    _pagedIllusts.update { oldIllusts ->
                        oldIllusts.toMutableList().apply {
                            add(result.illusts.toIllustModel())
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

    private fun List<PixivIllust>.toIllustModel() = this.filter {
        it.type == IllustType.Manga
    }.map { it.toIllustModel() }

    private fun PixivIllust.toIllustModel(): Illust {
        return Illust(
            id = id,
            title = title,
            caption = caption,
            createDate = createDate,
            coverPage = imageUrls.run {
                squareMedium ?: medium ?: large ?: original
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
            metaPages = metaSinglePage.originalImageUrl?.let {
                listOf(
                    ImageModel(
                        original = it
                    )
                )
            } ?: metaPages.map { metaPage ->
                metaPage.imageUrls.run {
                    ImageModel(
                        original = original,
                        large = large,
                        medium = medium,
                        squareMedium = squareMedium
                    )
                }
            },
            totalView = totalView,
            totalBookmarks = totalBookMarks,
            isBookMarked = isBookMarked,
            width = width,
            height = height,
            tags = tags.map { TagModel(it.name, it.translatedName) }
        )
    }

    companion object {

        const val BookmarkRestrictPublic: String = "public"

        const val BookmarkRestrictPrivate: String = "private"

    }
}