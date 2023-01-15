package fan.yumetsuki.yumepixiv.network.impl

import fan.yumetsuki.yumepixiv.network.PixivRecommendApi
import fan.yumetsuki.yumepixiv.network.model.RecommendResult
import fan.yumetsuki.yumepixiv.di.AppApiHttpClient
import fan.yumetsuki.yumepixiv.network.model.WalkThroughResult
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import javax.inject.Inject

class KtorPixivRecommendApi @Inject constructor(
    @AppApiHttpClient private val httpClient: HttpClient
): PixivRecommendApi {

    override suspend fun getRecommendIllusts(): RecommendResult {
        return httpClient.get("illust/recommended") {
            parameter("filter", "for_android")
            parameter("include_ranking_illusts", true)
            parameter("include_privacy_policy", true)
        }.body()
    }

    override suspend fun getWalkThroughIllust(): WalkThroughResult {
        return httpClient.get("walkthrough/illusts").body()
    }

    override suspend fun nextPageRecommendIllusts(nextUrl: String): RecommendResult {
        return httpClient.get(nextUrl).body()
    }

}