package fan.yumetsuki.yumepixiv.network

import fan.yumetsuki.yumepixiv.network.model.PixivOAuthTokenInfo

interface PixivAuthApi {

    suspend fun refreshToken(
        codeVerifier: String,
        code: String,
        grantType: String,
        redirectUri: String,
        clientId: String,
        clientSecret: String,
        includePolicy: Boolean
    ) : PixivOAuthTokenInfo

}