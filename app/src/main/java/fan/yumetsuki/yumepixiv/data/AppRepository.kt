package fan.yumetsuki.yumepixiv.data

import android.content.SharedPreferences
import fan.yumetsuki.yumepixiv.utils.string
import fan.yumetsuki.yumepixiv.utils.stringOrNull
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty



class AppRepository(
    private val sharedPreferences: SharedPreferences
) {

    private var _token: PixivToken? = null

    var accessToken: String? by sharedPreferences.stringOrNull(AccessToken)
        private set

    var refreshToken: String? by sharedPreferences.stringOrNull(RefreshToken)
        private set

    var token: PixivToken?
        set(value) {
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

    data class PixivToken(
        val accessToken: String,
        val refreshToken: String
    )

    companion object {
        internal const val AccessToken = "accessToken"

        internal const val RefreshToken = "refreshToken"
    }

}