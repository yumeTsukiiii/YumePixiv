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
        UiState(
            isReLoading = true,
            isLoadMore = false,
            illusts = emptyList(),
            rankingIllust = emptyList()
        )
    )

    private val _illusts = repository.illusts
    private val _rankingIllusts = repository.rankingIllust

    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        setupIllustFlow()
    }

    fun refreshIllusts() {
        viewModelScope.launch(Dispatchers.Main) {
            _uiState.update {
                UiState(isReLoading = true, isLoadMore = false, emptyList(), emptyList())
            }
            repository.refreshIllusts()
            _uiState.update {
                it.copy(isReLoading = false)
            }
        }
    }

    fun refreshIllustsIfEmpty() {
        if (uiState.value.illusts.isEmpty()) {
            refreshIllusts()
        }
    }

    fun nextPageIllust() {
        if (uiState.value.isLoadMore) {
            return
        }
        viewModelScope.launch(Dispatchers.Main) {
            _uiState.update { oldState ->
                oldState.copy(isLoadMore = true)
            }
            repository.nextPageIllust()
            _uiState.update { oldState ->
                oldState.copy(isLoadMore = false)
            }
        }
    }

    fun changeIllustBookmark(index: Int) {
        val chosenIllust = _uiState.value.illusts[index]
        viewModelScope.launch {
            _uiState.update { oldState ->
                oldState.copy(
                    illusts = oldState.illusts.toMutableList().apply {
                        this[index] = chosenIllust.copy(isFavorite = !chosenIllust.isFavorite)
                    }
                )
            }
            try {
                if (chosenIllust.isFavorite) {
                    repository.deleteIllustBookMark(_illusts.value[index])
                } else {
                    repository.addIllustBookMark(_illusts.value[index])
                }
            } catch (e: Throwable) {
                _uiState.update { oldState ->
                    oldState.copy(
                        illusts = oldState.illusts.toMutableList().apply {
                            this[index] = chosenIllust.copy()
                        }
                    )
                }
                e.printStackTrace()
            }
        }
    }

    private fun setupIllustFlow() {
        viewModelScope.launch {
            _illusts.collect {
                onIllustsUpdate(it)
            }
        }
        viewModelScope.launch {
            _rankingIllusts.collect {
                onRankingIllustsUpdate(it)
            }
        }
    }

    private fun onIllustsUpdate(illusts: List<Illust>) {
        _uiState.update { oldState ->
            oldState.copy(
                illusts = illusts.toIllustStates()
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
            height = height.dp
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
        val isReLoading: Boolean,
        val isLoadMore: Boolean,
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