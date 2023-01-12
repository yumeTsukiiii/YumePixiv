package fan.yumetsuki.yumepixiv.network

import fan.yumetsuki.yumepixiv.network.model.PixivOAuthTokenInfo

interface PixivAuthApi {

    suspend fun oauthLogin(
        codeVerifier: String,
        code: String,
        grantType: String,
        redirectUri: String,
        clientId: String,
        clientSecret: String,
        includePolicy: Boolean
    ) : PixivOAuthTokenInfo

    suspend fun refreshToken(
        refreshToken: String,
        grantType: String,
        clientId: String,
        clientSecret: String,
        includePolicy: Boolean
    ) : PixivOAuthTokenInfo

}