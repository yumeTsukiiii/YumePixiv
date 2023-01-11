package fan.yumetsuki.yumepixiv.network.impl

import fan.yumetsuki.yumepixiv.network.model.PixivOAuthTokenInfo
import fan.yumetsuki.yumepixiv.di.OAuthHttpClient
import fan.yumetsuki.yumepixiv.network.PixivAuthApi
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import javax.inject.Inject

class KtorPixivAuthApi @Inject constructor(
    @OAuthHttpClient private val httpClient: HttpClient
): PixivAuthApi {

    override suspend fun refreshToken(
        codeVerifier: String,
        code: String,
        grantType: String,
        redirectUri: String,
        clientId: String,
        clientSecret: String,
        includePolicy: Boolean
    ): PixivOAuthTokenInfo {
        val response = httpClient.submitForm(
            "auth/token",
            formParameters = Parameters.build {
                append(CodeVerifier, codeVerifier)
                append(Code, code)
                append(GrantType, grantType)
                append(RedirectUri, redirectUri)
                append(ClientId, clientId)
                append(ClientSecret, clientSecret)
                append(IncludePolicy, "$includePolicy")
            }
        )
        val bodyText = response.bodyAsText()
        println("bodyResponse: $bodyText")
        return response.body()
    }

    companion object {
        const val CodeVerifier = "code_verifier"
        const val Code = "code"
        const val GrantType = "grant_type"
        const val RedirectUri = "redirect_uri"
        const val ClientId = "client_id"
        const val ClientSecret = "client_secret"
        const val IncludePolicy = "includePolicy"
    }
}