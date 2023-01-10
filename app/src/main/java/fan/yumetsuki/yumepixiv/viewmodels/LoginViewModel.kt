package fan.yumetsuki.yumepixiv.viewmodels

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import fan.yumetsuki.yumepixiv.utils.generateCodeChallenge
import fan.yumetsuki.yumepixiv.utils.generateCodeVerifier

@Suppress("MemberVisibilityCanBePrivate")
class LoginViewModel : ViewModel() {

    fun jumpToLogin(context: Context) {
        val loginUrl = generateLoginUrl(
            generateCodeChallenge(generateCodeVerifier())
        )
        context.startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse(loginUrl))
        )
    }

    companion object {

        internal const val LoginUrlPrefix = "https://app-api.pixiv.net/web/v1/login?code_challenge="

        internal const val LoginUrlSuffix = "&code_challenge_method=S256&client=pixiv-android"

        internal fun generateLoginUrl(code_challenge: String) = "$LoginUrlPrefix$code_challenge$LoginUrlSuffix"

    }

}