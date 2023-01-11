package fan.yumetsuki.yumepixiv.viewmodels

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fan.yumetsuki.yumepixiv.data.AppRepository
import fan.yumetsuki.yumepixiv.utils.generateCodeChallenge
import fan.yumetsuki.yumepixiv.utils.generateCodeVerifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("MemberVisibilityCanBePrivate")
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val appRepository: AppRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(
        UiState(
            isRefreshingToken = false,
            isTokenUpdated = false
        )
    )

    val uiState = _uiState.asStateFlow()

    fun storeNewLoginToken(token: String) {
        _uiState.update { oldState ->
            oldState.copy(isRefreshingToken = true)
        }
        viewModelScope.launch(Dispatchers.Main) {
            appRepository.refreshToken(token)
            _uiState.update {
                UiState(isRefreshingToken = false, isTokenUpdated = true)
            }
        }
    }

    fun jumpToLogin(context: Context) {
        val loginUrl = generateLoginUrl(
            appRepository.pkce.codeChallenge
        )
        context.startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse(loginUrl))
        )
    }

    data class UiState(
        val isRefreshingToken: Boolean,
        val isTokenUpdated: Boolean
    )

    companion object {

        internal const val LoginUrlPrefix = "https://app-api.pixiv.net/web/v1/login?code_challenge="

        internal const val LoginUrlSuffix = "&code_challenge_method=S256&client=pixiv-android"

        internal fun generateLoginUrl(code_challenge: String) = "$LoginUrlPrefix$code_challenge$LoginUrlSuffix"

    }

}