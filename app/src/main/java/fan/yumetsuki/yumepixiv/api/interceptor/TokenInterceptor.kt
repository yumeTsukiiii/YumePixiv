package fan.yumetsuki.yumepixiv.api.interceptor

import fan.yumetsuki.yumepixiv.data.AppRepository
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*

internal const val OAuthProcessTokenError = "Error occurred at the OAuth process"

internal const val InvalidRefreshTokenError = "Invalid refresh token"

class TokenInterceptor(
    private val appRepository: AppRepository
): KtorHttpSendInterceptor {
    override suspend fun invoke(sender: Sender, request: HttpRequestBuilder): HttpClientCall {
        TODO("Not yet implemented")
    }
}