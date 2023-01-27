package fan.yumetsuki.yumepixiv.network.impl

import fan.yumetsuki.yumepixiv.network.PixivRecommendApi
import fan.yumetsuki.yumepixiv.network.model.IllustRecommendResult
import fan.yumetsuki.yumepixiv.di.AppApiHttpClient
import fan.yumetsuki.yumepixiv.network.model.MangaRecommendResult
import fan.yumetsuki.yumepixiv.network.model.RelatedResult
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

    override suspend fun getRecommendIllusts(): IllustRecommendResult {
        return httpClient.get("v1/illust/recommended") {
            parameter(Filter, ForAndroid)
            parameter(IncludeRankingIllusts, true)
            parameter(IncludePrivacyPolicy, true)
        }.body()
    }

    override suspend fun getWalkThroughIllust(): WalkThroughResult {
        return httpClient.get("v1/walkthrough/illusts").body()
    }

    override suspend fun relatedIllusts(illustId: Long): RelatedResult {
        return httpClient.get("v2/illust/related") {
            parameter(Filter, ForAndroid)
            parameter(IllustId, illustId)
        }.body()
    }

    override suspend fun nextPageRecommendIllusts(nextUrl: String): IllustRecommendResult {
        return httpClient.get(nextUrl).body()
    }

    override suspend fun nextRelatedRecommendIllusts(nextUrl: String): RelatedResult {
        return httpClient.get(nextUrl).body()
    }

    override suspend fun getRecommendMangas(): MangaRecommendResult {
        return httpClient.get("v1/manga/recommended") {
            parameter(Filter, ForAndroid)
            parameter(IncludeRankingIllusts, true)
            parameter(IncludePrivacyPolicy, true)
        }.body()
    }

    override suspend fun nextPageMangaIllusts(nextUrl: String): MangaRecommendResult {
        return httpClient.get(nextUrl).body()
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

    companion object {
        const val Filter = "filter"
        const val ForAndroid = "for_android"
        const val IllustId = "illust_id"
        const val Restrict = "restrict"
        const val IncludeRankingIllusts = "include_ranking_illusts"
        const val IncludePrivacyPolicy = "include_privacy_policy"
    }

}