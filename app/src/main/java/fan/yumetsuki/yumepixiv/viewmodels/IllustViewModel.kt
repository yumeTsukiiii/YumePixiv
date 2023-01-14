package fan.yumetsuki.yumepixiv.viewmodels

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fan.yumetsuki.yumepixiv.network.PixivRecommendApi
import fan.yumetsuki.yumepixiv.data.IllustRepository
import fan.yumetsuki.yumepixiv.data.model.Illust
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IllustViewModel @Inject constructor(
    pixivRecommendApi: PixivRecommendApi
): ViewModel() {

    private val repository = IllustRepository(pixivRecommendApi, viewModelScope)

    private val _uiState = MutableStateFlow(
        UiState(true, emptyList(), emptyList())
    )

    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        setupIllustFlow()
    }

    fun refreshIllusts() {
        viewModelScope.launch(Dispatchers.Main) {
            _uiState.update {
                UiState(isLoading = true, emptyList(), emptyList())
            }
            repository.refreshIllusts()
            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun refreshIllustsIfEmpty() {
        if (uiState.value.illusts.isEmpty()) {
            refreshIllusts()
        }
    }

    private fun setupIllustFlow() {
        viewModelScope.launch {
            repository.illusts.collect {
                onIllustsUpdate(it)
            }
        }
        viewModelScope.launch {
            repository.rankingIllust.collect {
                onRankingIllustsUpdate(it)
            }
        }
    }

    private fun onIllustsUpdate(illusts: List<Illust>) {
        _uiState.update { oldState ->
            oldState.copy(
                illusts = oldState.illusts + illusts.toIllustStates()
            )
        }
    }

    private fun onRankingIllustsUpdate(illusts: List<Illust>) {
        _uiState.update { oldState ->
            oldState.copy(
                rankingIllust = illusts.toRankingIllustStates()
            )
        }
    }

    private fun List<Illust>.toIllustStates(): List<UiState.IllustState> {
        return this.map { it.toIllustState() }
    }

    private fun List<Illust>.toRankingIllustStates(): List<UiState.RankingIllustState> {
        return this.map { it.toRankingIllustState() }
    }

    private fun Illust.toIllustState(): UiState.IllustState {
        return UiState.IllustState(
            imageUrl = coverPage,
            pageCount = pageCount,
            isFavorite = isBookMarked,
            height = (150..250).random().dp
        )
    }

    private fun Illust.toRankingIllustState(): UiState.RankingIllustState {
        return UiState.RankingIllustState(
            imageUrl = coverPage,
            pageCount = pageCount,
            isFavorite = isBookMarked,
            title = title,
            author = user.name,
            authorAvatar = user.avatar
        )
    }

    @Immutable
    data class UiState(
        val isLoading: Boolean,
        val illusts: List<IllustState>,
        val rankingIllust: List<RankingIllustState>
    ) {

        data class IllustState(
            val imageUrl: String?,
            val pageCount: Int,
            val isFavorite: Boolean,
            val height: Dp
        )

        data class RankingIllustState(
            val imageUrl: String?,
            val pageCount: Int,
            val isFavorite: Boolean,
            val title: String,
            val author: String,
            val authorAvatar: String?
        )
    }

}