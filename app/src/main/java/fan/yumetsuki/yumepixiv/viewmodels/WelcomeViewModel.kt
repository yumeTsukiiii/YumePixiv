package fan.yumetsuki.yumepixiv.viewmodels

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fan.yumetsuki.yumepixiv.data.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Suppress("MemberVisibilityCanBePrivate")
@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val appRepository: AppRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(
        UiState(
            isRefreshingToken = true,
            isTokenExisted = false
        )
    )

    val uiState = _uiState.asStateFlow()

    fun storeNewLoginToken(code: String) {
        _uiState.update { oldState ->
            oldState.copy(isRefreshingToken = true)
        }
        viewModelScope.launch(Dispatchers.Main) {
            appRepository.oauthLogin(code)
            _uiState.update {
                UiState(isRefreshingToken = false, isTokenExisted = appRepository.token != null)
            }
        }
    }

    fun checkTokenExisted() {
        viewModelScope.launch(Dispatchers.Main) {
            val token = withContext(Dispatchers.IO) {
                appRepository.token
            }
            _uiState.update { oldState ->
                oldState.copy(isRefreshingToken = false, isTokenExisted = token != null)
            }
        }
    }

    data class UiState(
        val isRefreshingToken: Boolean,
        val isTokenExisted: Boolean
    )

}