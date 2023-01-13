package fan.yumetsuki.yumepixiv.network.interceptor

import fan.yumetsuki.yumepixiv.data.AppRepository
import fan.yumetsuki.yumepixiv.utils.asText
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.api.*
import io.ktor.client.plugins.observer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.*
import io.ktor.utils.io.charsets.*
import io.ktor.utils.io.core.*

internal const val OAuthProcessTokenError = "Error occurred at the OAuth process"

internal const val InvalidRefreshTokenError = "Invalid refresh token"

@Suppress("MemberVisibilityCanBePrivate")
class TokenInterceptor(
    private val appRepository: AppRepository,
    private val authWhiteUrlParts: List<String> = listOf()
): HttpSendInterceptor {

    override suspend fun invoke(sender: Sender, request: HttpRequestBuilder): HttpClientCall {
        return internalInvoke(sender, request, true)
    }

    internal suspend fun internalInvoke(sender: Sender, request: HttpRequestBuilder, needRefreshToken: Boolean): HttpClientCall {
        if (authWhiteUrlParts.any { request.url.buildString().contains(it) }) {
            return sender.execute(request)
        }
        // refreshToken 不存在，直接需要重新登录，外部捕捉异常，基本逻辑上不会走到这里
        val pixivToken = appRepository.token ?: throw PixivUnAuthorizedException()
        request.header(HttpHeaders.Authorization, "Bearer ${pixivToken.accessToken}")
        val originalCall = sender.execute(request)
        val originalBody = originalCall.response.bodyAsChannel().toByteArray()
        return if (needRefreshToken && isUnauthorized(originalCall.response, originalBody)) {
            // 认证是不 ok 的，就去刷 refreshToken
            appRepository.refreshToken(pixivToken.refreshToken)
            internalInvoke(sender, request, false)
        } else {
            // 认证是 ok 的，直接返回 originalCall
            // body 只能读取一次，因此需要读一份出来，下一次用拷贝的数据传递
            originalCall.wrapWithContent(ByteReadChannel(originalBody))
        }
    }

    internal fun isUnauthorized(httpResponse: HttpResponse, originalBody: ByteArray): Boolean {
        if (httpResponse.status == HttpStatusCode.Unauthorized) {
            return true
        }
        val bodyText = originalBody.asText(httpResponse)
        if (bodyText.isEmpty()) {
            return true
        }
        return bodyText.contains(OAuthProcessTokenError) || bodyText.contains(InvalidRefreshTokenError)
    }

}

class PixivUnAuthorizedException : Exception(
    "PixivToken is not existed, need login"
)