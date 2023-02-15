package fan.yumetsuki.yumepixiv.viewmodels

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fan.yumetsuki.yumepixiv.network.PixivAppApi
import fan.yumetsuki.yumepixiv.data.IllustRepository
import fan.yumetsuki.yumepixiv.data.model.Illust
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IllustViewModel @Inject constructor(
    pixivAppApi: PixivAppApi
): ViewModel() {

    private val repository = IllustRepository(pixivAppApi, viewModelScope)

    private val _uiState = MutableStateFlow(
        UiState(
            isReLoading = true,
            isLoadMore = false,
            isError = false,
            currentIllustPage = -1,
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

    fun reloadIllusts() {
        viewModelScope.launch(Dispatchers.Main) {
            _uiState.update {
                it.copy(isReLoading = true, isLoadMore = false, currentIllustPage = -1)
            }
            try {
                repository.refreshIllusts()
                _uiState.update {
                    it.copy(isReLoading = false)
                }
            } catch (e: Throwable) {
                _uiState.update {
                    it.copy(isReLoading = false, isError = true, illusts = emptyList(), rankingIllust = emptyList())
                }
            }
        }
    }

    fun reloadIllustsIfEmpty() {
        if (uiState.value.illusts.isEmpty()) {
            reloadIllusts()
        }
    }

    fun nextPageIllust() {
        if (uiState.value.isLoadMore && !uiState.value.isError) {
            return
        }
        viewModelScope.launch(Dispatchers.Main) {
            _uiState.update { oldState ->
                oldState.copy(isLoadMore = true, isError = false)
            }
            try {
                repository.nextPageIllust()
                _uiState.update { oldState ->
                    oldState.copy(isLoadMore = false)
                }
            } catch (e: Throwable) {
                _uiState.update { oldState ->
                    oldState.copy(isLoadMore = true, isError = true)
                }
            }
        }
    }

    fun changeRankingIllustBookmark(index: Int) {
        val chosenIllust = _uiState.value.rankingIllust[index]
        viewModelScope.launch {
            _uiState.update { oldState ->
                oldState.copy(
                    rankingIllust = oldState.rankingIllust.toMutableList().apply {
                        this[index] = chosenIllust.copy(isBookmark = !chosenIllust.isBookmark)
                    }
                )
            }
            try {
                if (chosenIllust.isBookmark) {
                    repository.deleteIllustBookMark(_rankingIllusts.value[index])
                } else {
                    repository.addIllustBookMark(_rankingIllusts.value[index])
                }
            } catch (e: Throwable) {
                _uiState.update { oldState ->
                    oldState.copy(
                        rankingIllust = oldState.rankingIllust.toMutableList().apply {
                            this[index] = chosenIllust.copy()
                        }
                    )
                }
            }
        }
    }

    fun changeIllustBookmark(index: Int) {
        val chosenIllust = _uiState.value.illusts[index]
        viewModelScope.launch {
            _uiState.update { oldState ->
                oldState.copy(
                    illusts = oldState.illusts.toMutableList().apply {
                        this[index] = chosenIllust.copy(isBookmark = !chosenIllust.isBookmark)
                    }
                )
            }
            try {
                if (chosenIllust.isBookmark) {
                    repository.deleteIllustBookMark(_illusts.value[index])
                } else {
                    repository.addIllustBookMark(_illusts.value[index])
                    repository.relatedIllusts(_illusts.value[index]).also { illusts ->
                        _uiState.update { oldState ->
                            oldState.copy(
                                illusts = oldState.illusts.subList(0, index + 1) + illusts.toIllustStates()
                            )
                        }
                    }
                }
            } catch (e: Throwable) {
                _uiState.update { oldState ->
                    oldState.copy(
                        illusts = oldState.illusts.toMutableList().apply {
                            this[index] = chosenIllust.copy()
                        }
                    )
                }
            }
        }
    }

    fun openIllustDetail(index: Int) {
        if (index < 0 || index >=_uiState.value.illusts.size) {
            return
        }
        _uiState.update { oldState ->
            oldState.copy(currentIllustPage = index, currentSelectIllusts = oldState.illusts)
        }
    }

    fun openRankingIllustDetail(index: Int) {
        if (index < 0 || index >=_uiState.value.illusts.size) {
            return
        }
        _uiState.update { oldState ->
            oldState.copy(currentIllustPage = index, currentSelectIllusts = oldState.rankingIllust)
        }
    }

    fun closeIllustDetail() {
        _uiState.update { oldState ->
            oldState.copy(currentIllustPage = -1, currentSelectIllusts = null)
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
            val appended = illusts.size - oldState.illusts.size
            oldState.copy(
                illusts = if (appended <= 0) {
                    illusts.toIllustStates()
                } else {
                    oldState.illusts + illusts.subList(oldState.illusts.size, illusts.size).toIllustStates()
                }
            )
        }
    }

    private fun onRankingIllustsUpdate(illusts: List<Illust>) {
        _uiState.update { oldState ->
            val appended = illusts.size - oldState.rankingIllust.size
            oldState.copy(
                rankingIllust = if (appended <= 0) {
                    illusts.toRankingIllustStates()
                } else {
                    oldState.rankingIllust + illusts.subList(oldState.rankingIllust.size, illusts.size).toRankingIllustStates()
                }
            )
        }
    }

    private fun List<Illust>.toIllustStates(): List<UiState.IllustState> {
        return this.map { it.toIllustState((250..350).random().dp) }
    }

    private fun List<Illust>.toRankingIllustStates(): List<UiState.IllustState> {
        return this.map { it.toIllustState(156.dp) }
    }

    private fun Illust.toIllustState(cardHeight: Dp): UiState.IllustState {
        return UiState.IllustState(
            title = title,
            caption = caption,
            coverImageUrl = coverPage,
            metaImages = metaPages.mapNotNull { it.url },
            author = user.name,
            authorAvatarUrl = user.avatar,
            pageCount = pageCount,
            isBookmark = isBookMarked,
            totalViews = totalView,
            totalBookmarks = totalBookmarks,
            // TODO ISO String 转化为普通的时间，Java 8 工具类不可用，需要自己写
            createDate = createDate,
            tags = tags.map { UiState.IllustTagState(it.name, it.translatedName) },
            width = width,
            height = height,
            cardHeight = cardHeight
        )
    }

    @Immutable
    data class UiState(
        val isReLoading: Boolean,
        val isLoadMore: Boolean,
        val isError: Boolean,
        val currentIllustPage: Int,
        val illusts: List<IllustState>,
        val rankingIllust: List<IllustState>,
        val currentSelectIllusts: List<IllustState>? = null,
    ) {

        data class IllustState(
            val title: String,
            val caption: String,
            val coverImageUrl: String?,
            val metaImages: List<String>,
            val author: String,
            val authorAvatarUrl: String?,
            val pageCount: Int,
            val isBookmark: Boolean,
            val totalViews: Int,
            val totalBookmarks: Int,
            val createDate: String,
            val tags: List<IllustTagState>,
            val width: Int,
            val height: Int,
            val cardHeight: Dp
        )

        data class IllustTagState(
            val name: String,
            val translateName: String?
        )
    }

}

val IllustViewModel.UiState.isOpenIllustDetail: Boolean
    get() = !isReLoading && currentSelectIllusts != null && currentIllustPage >= 0 && currentIllustPage < illusts.size