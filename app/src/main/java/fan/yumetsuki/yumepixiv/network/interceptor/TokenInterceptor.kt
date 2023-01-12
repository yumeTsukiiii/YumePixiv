package fan.yumetsuki.yumepixiv.network.interceptor

import fan.yumetsuki.yumepixiv.data.AppRepository
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.api.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

internal const val OAuthProcessTokenError = "Error occurred at the OAuth process"

internal const val InvalidRefreshTokenError = "Invalid refresh token"

@Suppress("MemberVisibilityCanBePrivate")
class TokenInterceptor(
    private val appRepository: AppRepository
): HttpSendInterceptor {

    override suspend fun invoke(sender: Sender, request: HttpRequestBuilder): HttpClientCall {
        return internalInvoke(sender, request, true)
    }

    internal suspend fun internalInvoke(sender: Sender, request: HttpRequestBuilder, needRefreshToken: Boolean): HttpClientCall {
        // refreshToken 不存在，直接需要重新登录，外部捕捉异常，基本逻辑上不会走到这里
        val pixivToken = appRepository.token ?: throw PixivUnAuthorizedException()
        request.header(HttpHeaders.Authorization, "Bearer ${pixivToken.accessToken}")
        val originalCall = sender.execute(request)
        return if (needRefreshToken && isUnauthorized(originalCall.save().response)) {
            // 认证是不 ok 的，就去刷 refreshToken
            appRepository.refreshToken(pixivToken.refreshToken)
            internalInvoke(sender, request, false)
        } else {
            // 认证是 ok 的，直接返回 originalCall
            originalCall
        }
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

class PixivUnAuthorizedException : Exception(
    "PixivToken is not existed, need login"
)