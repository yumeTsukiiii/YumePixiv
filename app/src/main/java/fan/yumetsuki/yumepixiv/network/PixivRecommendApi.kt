package fan.yumetsuki.yumepixiv.network

import fan.yumetsuki.yumepixiv.network.model.RecommendResult
import fan.yumetsuki.yumepixiv.network.model.WalkThroughResult

interface PixivRecommendApi {

    suspend fun getRecommendIllusts(): RecommendResult

    suspend fun getWalkThroughIllust(): WalkThroughResult

    suspend fun nextPageRecommendIllusts(nextUrl: String): RecommendResult

}