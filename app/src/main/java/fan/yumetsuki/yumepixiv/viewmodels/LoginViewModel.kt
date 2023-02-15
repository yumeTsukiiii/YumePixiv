package fan.yumetsuki.yumepixiv.viewmodels

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fan.yumetsuki.yumepixiv.data.AppRepository
import fan.yumetsuki.yumepixiv.data.IllustRepository
import fan.yumetsuki.yumepixiv.network.PixivAppApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("MemberVisibilityCanBePrivate")
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val appRepository: AppRepository,
    appRecommendApi: PixivAppApi
): ViewModel() {

    private val illustRepository = IllustRepository(appRecommendApi, viewModelScope)

    private val _uiState = MutableStateFlow(
        UiState(workThroughImages = emptyList())
    )

    val uiState = _uiState.asStateFlow()

    fun requestWorkThroughIllusts() {
        viewModelScope.launch(Dispatchers.Main) {
            val illusts = runCatching {
                illustRepository.getWalkthroughIllusts()
            }.getOrNull()
            _uiState.update {
                UiState(
                    workThroughImages = illusts?.map { it.coverPage!! }
                )
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
        val workThroughImages: List<String>?
    )

    companion object {

        internal const val LoginUrlPrefix = "https://app-api.pixiv.net/web/v1/login?code_challenge="

        internal const val LoginUrlSuffix = "&code_challenge_method=S256&client=pixiv-android"

        internal fun generateLoginUrl(code_challenge: String) = "$LoginUrlPrefix$code_challenge$LoginUrlSuffix"

    }

}