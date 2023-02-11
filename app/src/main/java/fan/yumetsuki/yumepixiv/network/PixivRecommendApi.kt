package fan.yumetsuki.yumepixiv.network

import fan.yumetsuki.yumepixiv.network.model.*

interface PixivRecommendApi {

    /*-----------------------插画相关-----------------------*/

    suspend fun getRecommendIllusts(): IllustRecommendResult

    suspend fun getWalkThroughIllust(): WalkThroughResult

    suspend fun relatedIllusts(illustId: Long): RelatedResult

    suspend fun nextPageRecommendIllusts(nextUrl: String): IllustRecommendResult

    suspend fun nextRelatedRecommendIllusts(nextUrl: String): RelatedResult

    /*-----------------------漫画相关-----------------------*/

    suspend fun getRecommendMangas(): MangaRecommendResult

    suspend fun nextPageMangaIllusts(nextUrl: String): MangaRecommendResult

    /*-----------------------小说相关-----------------------*/

    suspend fun getRecommendNovels(): NovelRecommendResult

    suspend fun nextPageNovels(nextUrl: String): NovelRecommendResult

    /*-----------------------操作相关-----------------------*/

    suspend fun addIllustBookMark(illustId: Long, restrict: String)

    suspend fun deleteIllustBookMark(illustId: Long)

    suspend fun addNovelBookmark(novelId: Long, restrict: String)

    suspend fun deleteNovelBookmark(novelId: Long)
}