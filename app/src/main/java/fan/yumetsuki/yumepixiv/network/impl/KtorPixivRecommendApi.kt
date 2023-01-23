package fan.yumetsuki.yumepixiv.network.impl

import fan.yumetsuki.yumepixiv.network.PixivRecommendApi
import fan.yumetsuki.yumepixiv.network.model.RecommendResult
import fan.yumetsuki.yumepixiv.di.AppApiHttpClient
import fan.yumetsuki.yumepixiv.network.model.WalkThroughResult
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import javax.inject.Inject

class KtorPixivRecommendApi @Inject constructor(
    @AppApiHttpClient private val httpClient: HttpClient
): PixivRecommendApi {

    override suspend fun getRecommendIllusts(): RecommendResult {
        return httpClient.get("v1/illust/recommended") {
            parameter("filter", "for_android")
            parameter("include_ranking_illusts", true)
            parameter("include_privacy_policy", true)
        }.body()
    }

    override suspend fun getWalkThroughIllust(): WalkThroughResult {
        return httpClient.get("v1/walkthrough/illusts").body()
    }

    override suspend fun addIllustBookMark(illustId: Long, restrict: String) {
        httpClient.submitForm(
            "v2/illust/bookmark/add",
            formParameters = Parameters.build {
                append(IllustId, illustId.toString())
                append(Restrict, restrict)
            }
        )
    }

    override suspend fun deleteIllustBookMark(illustId: Long) {
        httpClient.submitForm(
            "v1/illust/bookmark/delete",
            formParameters = Parameters.build {
                append(IllustId, illustId.toString())
            }
        )
    }

    override suspend fun nextPageRecommendIllusts(nextUrl: String): RecommendResult {
        return httpClient.get(nextUrl).body()
    }

    companion object {
        const val IllustId: String = "illust_id"
        const val Restrict = "restrict"
    }

}