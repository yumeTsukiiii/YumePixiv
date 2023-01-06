package fan.yumetsuki.yumepixiv.api

import fan.yumetsuki.yumepixiv.api.model.RecommendResult

interface PixivRecommendApi {

    suspend fun getRecommendIllust(): RecommendResult

}