package fan.yumetsuki.yumepixiv.viewmodels

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import fan.yumetsuki.yumepixiv.data.NovelRepository
import fan.yumetsuki.yumepixiv.network.PixivRecommendApi
import fan.yumetsuki.yumepixiv.data.model.Novel
import fan.yumetsuki.yumepixiv.ui.screen.novelviewer.navigateToNovelViewer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NovelViewModel @Inject constructor(
    pixivRecommendApi: PixivRecommendApi,
): ViewModel() {

    private val repository = NovelRepository(pixivRecommendApi, viewModelScope)

    private val _uiState = MutableStateFlow(
        UiState(
            isReLoading = true,
            isLoadMore = false,
            currentIllustPage = -1,
            novels = emptyList(),
            rankingNovels = emptyList()
        )
    )

    private val _novels = repository.novels
    private val _rankingNovels = repository.rankingNovels

    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        setupNovelFlow()
    }

    fun navigateToRankingNovelDetail(index: Int, navController: NavController) {
        navigateToNovelViewer(_rankingNovels.value[index].id, navController)
    }

    fun navigateToNovelDetail(index: Int, navController: NavController) {
        navigateToNovelViewer(_novels.value[index].id, navController)
    }

    fun reloadNovels() {
        viewModelScope.launch(Dispatchers.Main) {
            _uiState.update {
                it.copy(isReLoading = true, isLoadMore = false, currentIllustPage = -1)
            }
            repository.refreshNovels()
            _uiState.update {
                it.copy(isReLoading = false)
            }
        }
    }

    fun reloadNovelsIfEmpty() {
        if (uiState.value.novels.isEmpty()) {
            reloadNovels()
        }
    }

    fun nextPageNovel() {
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

    fun changeRankingNovelBookmark(index: Int) {
        val chosenNovel = _uiState.value.rankingNovels[index]
        viewModelScope.launch {
            _uiState.update { oldState ->
                oldState.copy(
                    rankingNovels = oldState.rankingNovels.toMutableList().apply {
                        this[index] = chosenNovel.copy(isBookmark = !chosenNovel.isBookmark)
                    }
                )
            }
            try {
                if (chosenNovel.isBookmark) {
                    repository.deleteNovelBookMark(_rankingNovels.value[index])
                } else {
                    repository.deleteNovelBookMark(_rankingNovels.value[index])
                }
            } catch (e: Throwable) {
                _uiState.update { oldState ->
                    oldState.copy(
                        rankingNovels = oldState.rankingNovels.toMutableList().apply {
                            this[index] = chosenNovel.copy()
                        }
                    )
                }
            }
        }
    }

    fun changeNovelBookmark(index: Int) {
        val chosenNovel = _uiState.value.novels[index]
        viewModelScope.launch {
            _uiState.update { oldState ->
                oldState.copy(
                    novels = oldState.novels.toMutableList().apply {
                        this[index] = chosenNovel.copy(isBookmark = !chosenNovel.isBookmark)
                    }
                )
            }
            try {
                if (chosenNovel.isBookmark) {
                    repository.deleteNovelBookMark(_novels.value[index])
                } else {
                    repository.addNovelBookMark(_novels.value[index])
                }
            } catch (e: Throwable) {
                _uiState.update { oldState ->
                    oldState.copy(
                        novels = oldState.novels.toMutableList().apply {
                            this[index] = chosenNovel.copy()
                        }
                    )
                }
            }
        }
    }

    private fun navigateToNovelViewer(novelId: Long, navController: NavController) {
        navController.navigateToNovelViewer(novelId)
    }

    private fun setupNovelFlow() {
        viewModelScope.launch {
            _novels.collect {
                onNovelsUpdate(it)
            }
        }
        viewModelScope.launch {
            _rankingNovels.collect {
                onNovelsIllustsUpdate(it)
            }
        }
    }

    private fun onNovelsUpdate(novels: List<Novel>) {
        _uiState.update { oldState ->
            val appended = novels.size - oldState.novels.size
            oldState.copy(
                novels = if (appended <= 0) {
                    novels.toNovelStates()
                } else {
                    oldState.novels + novels.subList(oldState.novels.size, novels.size).toNovelStates()
                }
            )
        }
    }

    private fun onNovelsIllustsUpdate(novels: List<Novel>) {
        _uiState.update { oldState ->
            val appended = novels.size - oldState.rankingNovels.size
            oldState.copy(
                rankingNovels = if (appended <= 0) {
                    novels.toRankingNovelStates()
                } else {
                    oldState.rankingNovels + novels.subList(oldState.rankingNovels.size, novels.size).toRankingNovelStates()
                }
            )
        }
    }

    private fun List<Novel>.toNovelStates(): List<UiState.NovelState> {
        return this.map { it.toNovelState(276.dp) }
    }

    private fun List<Novel>.toRankingNovelStates(): List<UiState.NovelState> {
        return this.map { it.toNovelState(196.dp) }
    }

    private fun Novel.toNovelState(cardHeight: Dp): UiState.NovelState {
        return UiState.NovelState(
            title = title,
            coverImageUrl = coverPage,
            author = user.name,
            authorAvatarUrl = user.avatar,
            isBookmark = isBookMarked,
            totalBookmarks = totalBookmarks,
            tags = tags.map {
                UiState.NovelTagState(it.name, it.translatedName)
            },
            wordCount = textLength,
            series = series?.title,
            cardHeight = cardHeight
        )
    }

    @Immutable
    data class UiState(
        val isReLoading: Boolean,
        val isLoadMore: Boolean,
        val currentIllustPage: Int,
        val novels: List<NovelState>,
        val rankingNovels: List<NovelState>,
    ) {

        data class NovelState(
            val title: String,
            val coverImageUrl: String?,
            val author: String,
            val authorAvatarUrl: String?,
            val isBookmark: Boolean,
            val totalBookmarks: Int,
            val tags: List<NovelTagState>,
            val wordCount: Int,
            val series: String?,
            val cardHeight: Dp
        )

        data class NovelTagState(
            val name: String,
            val translateName: String?
        )
    }

}