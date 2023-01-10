package fan.yumetsuki.yumepixiv.network

import fan.yumetsuki.yumepixiv.network.model.RecommendResult

interface PixivRecommendApi {

    suspend fun getRecommendIllust(): RecommendResult

    suspend fun nextPageRecommendIllust(nextUrl: String): RecommendResult

}