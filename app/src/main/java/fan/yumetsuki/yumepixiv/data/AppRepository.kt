package fan.yumetsuki.yumepixiv.data

import android.content.SharedPreferences
import fan.yumetsuki.yumepixiv.network.PixivAuthApi
import fan.yumetsuki.yumepixiv.utils.generateCodeChallenge
import fan.yumetsuki.yumepixiv.utils.generateCodeVerifier
import fan.yumetsuki.yumepixiv.utils.stringOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Suppress("MemberVisibilityCanBePrivate")
class AppRepository(
    sharedPreferences: SharedPreferences,
    private val pixivAuthApi: PixivAuthApi
) {

    private var _token: PixivToken? = null

    private lateinit var _pkce: PKCE

    val pkce: PKCE
        get() {
            return if (this::_pkce.isInitialized) {
                _pkce
            } else {
                val codeVerifier = generateCodeVerifier()
                val codeChallenge = generateCodeChallenge(codeVerifier)
                _pkce = PKCE(codeVerifier, codeChallenge)
                _pkce
            }
        }

    var accessToken: String? by sharedPreferences.stringOrNull(AccessToken)
        internal set

    var refreshToken: String? by sharedPreferences.stringOrNull(RefreshToken)
        internal set

    var token: PixivToken?
        internal set(value) {
            accessToken = value?.accessToken
            refreshToken = value?.refreshToken
            if (value != null) {
                _token = value
            }
        }
        get() {
            if (_token != null) {
                return _token
            }
            val accessTokenValue = accessToken
            val refreshTokenValue = refreshToken
            return if (accessTokenValue != null && refreshTokenValue != null) {
                PixivToken(accessTokenValue, refreshTokenValue)
            } else {
                null
            }
        }

    suspend fun oauthLogin(
        code: String
    ) {
        val tokenResult = withContext(Dispatchers.IO) {
            pixivAuthApi.oauthLogin(
                pkce.codeVerifier, code,
                GrantTypeAuthorizationCode, RedirectUri, ClientId, ClientSecret, IncludePolicy
            )
        }
        token = PixivToken(tokenResult.accessToken, tokenResult.refreshToken)
    }

    suspend fun refreshToken(
        refreshToken: String
    ) {
        val tokenResult = withContext(Dispatchers.IO) {
            pixivAuthApi.refreshToken(refreshToken, GrantTypeRefreshToken, ClientId, ClientSecret, IncludePolicy)
        }
        token = PixivToken(tokenResult.accessToken, tokenResult.refreshToken)
    }

    data class PixivToken(
        val accessToken: String,
        val refreshToken: String
    )

    data class PKCE(
        val codeVerifier: String,
        val codeChallenge: String
    )

    companion object {
        const val AccessToken = "accessToken"

        const val RefreshToken = "refreshToken"

        const val GrantTypeAuthorizationCode = "authorization_code"

        const val GrantTypeRefreshToken = "refresh_token"

        const val RedirectUri = "https://app-api.pixiv.net/web/v1/users/auth/pixiv/callback"

        const val ClientId = "MOBrBDS8blbauoSck0ZfDbtuzpyT"

        const val ClientSecret = "lsACyCD94FhDUtGTXi3QzcFE2uU1hqtDaKeqrdwj"

        const val IncludePolicy = true
    }

}