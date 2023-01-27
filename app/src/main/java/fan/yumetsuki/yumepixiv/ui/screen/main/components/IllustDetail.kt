package fan.yumetsuki.yumepixiv.ui.screen.main.components

import android.annotation.SuppressLint
import android.graphics.Paint.Align
import android.graphics.Typeface
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.HeartBroken
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.google.accompanist.flowlayout.FlowRow
import fan.yumetsuki.yumepixiv.ui.components.BottomSheet
import fan.yumetsuki.yumepixiv.ui.components.BottomSheetScaffold
import fan.yumetsuki.yumepixiv.ui.components.YumePixivTip
import fan.yumetsuki.yumepixiv.utils.pixivImageRequestBuilder
import fan.yumetsuki.yumepixiv.utils.spannableStringToAnnotatedString
import java.text.SimpleDateFormat
import java.util.Calendar

data class IllustDetailImage(
    val url: String,
    val ratio: Float
)

data class IllustDetailTag(
    val name: String,
    val translatedName: String?
)

data class IllustDetail(
    val title: String,
    val caption: String,
    val images: List<IllustDetailImage>,
    val author: String,
    val authorAvatarUrl: String,
    val isBookmark: Boolean,
    val totalViews: Int,
    val totalBookmark: Int,
    val createDate: String,
    val tags: List<IllustDetailTag>
)

@Composable
private fun illustDetailImageRequestBuilder(imageUrl: String): ImageRequest.Builder {
    return pixivImageRequestBuilder(imageUrl = imageUrl)
        .crossfade(500)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorListItem(
    author: String,
    authorAvatarUrl: String,
    createDate: String,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier,
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        ),
        leadingContent = {
            AsyncImage(
                model = illustDetailImageRequestBuilder(imageUrl = authorAvatarUrl).build(),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(
                        CircleShape
                    ),
                contentDescription = null
            )
        },
        headlineText = {
            Text(
                text = author,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        trailingContent = {
            OutlinedButton(
                onClick = {}
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Text(text = "关注", modifier = Modifier.padding(start = ButtonDefaults.IconSpacing))
            }
        },
        supportingText = {
            Text(
                text = "$createDate 投递",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IllustDetail(
    illustDetail: IllustDetail,
    modifier: Modifier = Modifier,
    foldHeight: Dp = 124.dp,
    bottomSheetPadding: Dp = 64.dp,
    density: Density = LocalDensity.current,
    onPageChange: ((page: Int) -> Unit)? = null
) {

    val contentImagesState = rememberLazyListState()
    val firstVisibleImageIndex by remember {
        derivedStateOf {
            contentImagesState.firstVisibleItemIndex
        }
    }

    LaunchedEffect(firstVisibleImageIndex) {
        onPageChange?.invoke(firstVisibleImageIndex)
    }

    BoxWithConstraints {
        BottomSheetScaffold(
            foldHeight = foldHeight,
            expandHeight = this@BoxWithConstraints.maxHeight - bottomSheetPadding,
            modifier = modifier,
            bottomSheet = {
                BottomSheet {

                    AuthorListItem(
                        author = illustDetail.author,
                        authorAvatarUrl = illustDetail.authorAvatarUrl,
                        createDate = illustDetail.createDate,
                    )

                    Column(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = illustDetail.title, style = MaterialTheme.typography.headlineMedium.merge(
                            LocalTextStyle.current
                        ))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            TextButton(onClick = { /*TODO*/ }) {
                                Icon(imageVector = Icons.Filled.Visibility, contentDescription = null)
                                Text(text = "${illustDetail.totalViews} 看过", modifier = Modifier.padding(start = ButtonDefaults.IconSpacing))
                            }

                            TextButton(onClick = { /*TODO*/ }) {
                                Icon(imageVector = Icons.Filled.Favorite, contentDescription = null)
                                Text(text = "${illustDetail.totalBookmark} 收藏", modifier = Modifier.padding(start = ButtonDefaults.IconSpacing))
                            }
                        }

                        FlowRow(
                            mainAxisSpacing = 8.dp
                        ) {
                            illustDetail.tags.forEach { tag ->
                                AssistChip(
                                    onClick = { /*TODO*/ },
                                    label = {
                                        Text(text = "${tag.name}${tag.translatedName?.let {
                                            " / $it"
                                        }?:""}")
                                    }
                                )
                            }
                        }

                        Text(
                            text = spannableStringToAnnotatedString(
                                HtmlCompat.fromHtml(illustDetail.caption, HtmlCompat.FROM_HTML_MODE_COMPACT),
                                density = density
                            ),
                            style = MaterialTheme.typography.bodyMedium.merge(
                                LocalTextStyle.current
                            ),
                            modifier = Modifier.weight(1f),
                            overflow = TextOverflow.Ellipsis
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(onClick = { /*TODO*/ }) {
                                Icon(imageVector = Icons.Outlined.Comment, contentDescription = null, modifier = Modifier.padding(
                                    ButtonDefaults.IconSpacing
                                ))
                                Text(text = "评论")
                            }

                            TextButton(onClick = { /*TODO*/ }) {
                                Icon(imageVector = Icons.Outlined.Article, contentDescription = null, modifier = Modifier.padding(
                                    ButtonDefaults.IconSpacing
                                ))
                                Text(text = "相关作品")
                            }

                            TextButton(onClick = { /*TODO*/ }) {
                                Icon(imageVector = Icons.Outlined.Download, contentDescription = null, modifier = Modifier.padding(
                                    ButtonDefaults.IconSpacing
                                ))
                                Text(text = "下载")
                            }
                        }
                    }
                }
            }
        ) {
            if (illustDetail.images.isEmpty()) {
                YumePixivTip(
                    text = "奇了怪了这个人都没图片啊喂",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 96.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = (foldHeight - 16.dp).coerceAtLeast(0.dp)),
                    verticalArrangement = Arrangement.Center
                ) {
                    items(illustDetail.images) { image ->
                        BoxWithConstraints {
                            SubcomposeAsyncImage(
                                model = illustDetailImageRequestBuilder(imageUrl = image.url).build(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(this.maxWidth * image.ratio),
                                contentScale = ContentScale.Crop,
                                contentDescription = null,
                                loading = {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Preview
@Composable
fun IllustDetailPreview() {

    val imageUrl = "https://tse4-mm.cn.bing.net/th/id/OIP-C.P5Y9Ph3AUf7NSr9GzYDHjAHaEo?w=280&h=180&c=7&r=0&o=5&dpr=2&pid=1.7"

    IllustDetail(
        title = "美少女www！！",
        caption = "这是一段很长很长的插画描述".repeat(1),
        images = (0..8).map {
            IllustDetailImage(
                url = imageUrl,
                ratio = 1f
            )
        },
        author = "二阶堂梦月",
        authorAvatarUrl = imageUrl,
        isBookmark = false,
        totalViews = 123456,
        totalBookmark = 114514,
        createDate = SimpleDateFormat(
            "yyyy-MM-dd HH:mm"
        ).format(Calendar.getInstance().time),
        tags = listOf(
            IllustDetailTag(
                name = "Irotori",
                translatedName = "五彩斑斓的世界"
            ),
            IllustDetailTag(
                name = "FGO",
                translatedName = "FFFGO"
            ),
            IllustDetailTag(
                name = "赛马娘",
                translatedName = "滴滴滴滴"
            ),
            IllustDetailTag(
                name = "holyShift",
                translatedName = "2333"
            ),
            IllustDetailTag(
                name = "Irotori",
                translatedName = "五彩斑斓的世界"
            ),
            IllustDetailTag(
                name = "FGO",
                translatedName = "FFFGO"
            ),
            IllustDetailTag(
                name = "赛马娘",
                translatedName = "滴滴滴滴"
            ),
            IllustDetailTag(
                name = "holyShift",
                translatedName = "2333"
            ),
            IllustDetailTag(
                name = "holyShift",
                translatedName = "2333"
            ),
            IllustDetailTag(
                name = "holyShift",
                translatedName = "2333"
            ),
            IllustDetailTag(
                name = "holyShift",
                translatedName = "2333"
            )
        ).shuffled()
    )

}