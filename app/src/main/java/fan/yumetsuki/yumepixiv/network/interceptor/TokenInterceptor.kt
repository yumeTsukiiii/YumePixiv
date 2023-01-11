package fan.yumetsuki.yumepixiv.network.interceptor

import fan.yumetsuki.yumepixiv.data.AppRepository
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

internal const val OAuthProcessTokenError = "Error occurred at the OAuth process"

internal const val InvalidRefreshTokenError = "Invalid refresh token"

@Suppress("MemberVisibilityCanBePrivate")
class TokenInterceptor(
    private val appRepository: AppRepository
): KtorHttpSendInterceptor {
    override suspend fun invoke(sender: Sender, request: HttpRequestBuilder): HttpClientCall {
        val pixivAccessToken = appRepository.accessToken
        if (pixivAccessToken != null) {
            request.header(HttpHeaders.Authorization, "Bearer $pixivAccessToken")
        }
        val originalCall = sender.execute(request)
        if (isUnauthorized(originalCall.response)) {
            // 认证是不 ok 的，如果 refreshToken 存在，就去刷 refreshToken
        } else {
            // 认证是 ok 的，直接返回 originalCall
        }
        TODO("Not yet implemented")
    }

    internal suspend fun isUnauthorized(httpResponse: HttpResponse): Boolean {
        if (httpResponse.status == HttpStatusCode.Unauthorized) {
            return true
        }
        val bodyText = httpResponse.bodyAsText()
        if (bodyText.isEmpty()) {
            return true
        }
        return bodyText.contains(OAuthProcessTokenError) || bodyText.contains(InvalidRefreshTokenError)
    }
}