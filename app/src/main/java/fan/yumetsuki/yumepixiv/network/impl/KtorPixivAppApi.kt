package fan.yumetsuki.yumepixiv.network.impl

import fan.yumetsuki.yumepixiv.network.PixivAppApi
import fan.yumetsuki.yumepixiv.di.AppApiHttpClient
import fan.yumetsuki.yumepixiv.network.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import javax.inject.Inject

class KtorPixivAppApi @Inject constructor(
    @AppApiHttpClient private val httpClient: HttpClient
): PixivAppApi {

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

    override suspend fun getRecommendNovels(): NovelRecommendResult {
        return httpClient.get("v1/novel/recommended") {
            parameter(IncludeRankingNovels, true)
            parameter(IncludePrivacyPolicy, true)
        }.body()
    }

    override suspend fun nextPageNovels(nextUrl: String): NovelRecommendResult {
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

    override suspend fun addNovelBookmark(novelId: Long, restrict: String) {
        httpClient.submitForm(
            "v2/novel/bookmark/add",
            formParameters = Parameters.build {
                append(NovelId, novelId.toString())
                append(Restrict, restrict)
            }
        )
    }

    override suspend fun deleteNovelBookmark(novelId: Long) {
        httpClient.submitForm(
            "v1/novel/bookmark/delete",
            formParameters = Parameters.build {
                append(NovelId, novelId.toString())
            }
        )
    }

    override suspend fun getBookmarksIllusts(
        userId: Long,
        restrict: String,
        maxBookmarkId: Long?,
        tag: String?
    ): PixivBookmarksIllusts {
        return httpClient.get("v1/user/bookmarks/illust") {
            parameter(UserId, userId)
            parameter(Restrict, restrict)
            if (maxBookmarkId != null) {
                parameter(MaxBookmarkId, maxBookmarkId)
            }
            if (tag != null) {
                parameter(Tag, tag)
            }
        }.body()
    }

    override suspend fun getBookmarksNovels(
        userId: Long,
        restrict: String,
        maxBookmarkId: Long?,
        tag: String?
    ): PixivBookmarksNovels {
        return httpClient.get("v1/user/bookmarks/novel") {
            parameter(UserId, userId)
            parameter(Restrict, restrict)
            if (maxBookmarkId != null) {
                parameter(MaxBookmarkId, maxBookmarkId)
            }
            if (tag != null) {
                parameter(Tag, tag)
            }
        }.body()
    }

    companion object {
        const val Filter = "filter"
        const val ForAndroid = "for_android"
        const val IllustId = "illust_id"
        const val NovelId = "illust_id"
        const val Restrict = "restrict"
        const val IncludeRankingIllusts = "include_ranking_illusts"
        const val IncludeRankingNovels = "include_ranking_novels"
        const val IncludePrivacyPolicy = "include_privacy_policy"
        const val UserId = "userId"
        const val MaxBookmarkId = "max_bookmark_id"
        const val Tag = "tag"
    }

}