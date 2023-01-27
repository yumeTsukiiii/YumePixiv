package fan.yumetsuki.yumepixiv.viewmodels

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fan.yumetsuki.yumepixiv.network.PixivRecommendApi
import fan.yumetsuki.yumepixiv.data.MangaRepository
import fan.yumetsuki.yumepixiv.data.model.Illust
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MangaViewModel @Inject constructor(
    pixivRecommendApi: PixivRecommendApi
): ViewModel() {

    private val repository = MangaRepository(pixivRecommendApi, viewModelScope)

    private val _uiState = MutableStateFlow(
        UiState(
            isReLoading = true,
            isLoadMore = false,
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
                UiState(isReLoading = true, isLoadMore = false, currentIllustPage = -1, emptyList(), emptyList())
            }
            repository.refreshIllusts()
            _uiState.update {
                it.copy(isReLoading = false)
            }
        }
    }

    fun reloadIllustsIfEmpty() {
        if (uiState.value.illusts.isEmpty()) {
            reloadIllusts()
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
            oldState.copy(currentIllustPage = index)
        }
    }

    fun closeIllustDetail() {
        _uiState.update { oldState ->
            oldState.copy(currentIllustPage = -1)
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
        return this.map { it.toIllustState() }
    }

    private fun List<Illust>.toRankingIllustStates(): List<UiState.RankingIllustState> {
        return this.map { it.toRankingIllustState() }
    }

    private fun Illust.toIllustState(): UiState.IllustState {
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
            cardHeight = 350.dp
        )
    }

    private fun Illust.toRankingIllustState(): UiState.RankingIllustState {
        return UiState.RankingIllustState(
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
        )
    }

    @Immutable
    data class UiState(
        val isReLoading: Boolean,
        val isLoadMore: Boolean,
        val currentIllustPage: Int,
        val illusts: List<IllustState>,
        val rankingIllust: List<RankingIllustState>
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

        data class RankingIllustState(
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
            val height: Int
        )
    }

}

val MangaViewModel.UiState.isOpenIllustDetail: Boolean
    get() = !isReLoading && illusts.isNotEmpty() && currentIllustPage >= 0 && currentIllustPage < illusts.size