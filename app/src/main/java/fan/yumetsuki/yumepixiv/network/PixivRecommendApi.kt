package fan.yumetsuki.yumepixiv.network

import fan.yumetsuki.yumepixiv.network.model.IllustRecommendResult
import fan.yumetsuki.yumepixiv.network.model.MangaRecommendResult
import fan.yumetsuki.yumepixiv.network.model.RelatedResult
import fan.yumetsuki.yumepixiv.network.model.WalkThroughResult

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

    /*-----------------------操作相关-----------------------*/

    suspend fun addIllustBookMark(illustId: Long, restrict: String)

    suspend fun deleteIllustBookMark(illustId: Long)
}