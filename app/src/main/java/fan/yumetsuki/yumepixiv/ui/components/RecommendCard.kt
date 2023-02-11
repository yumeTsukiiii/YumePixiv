package fan.yumetsuki.yumepixiv.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.flowlayout.FlowRow
import fan.yumetsuki.yumepixiv.R

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FavoriteIcon(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    favoriteTint: Color = Color.Red,
    notFavoriteTint: Color = LocalContentColor.current
) {
    Box(modifier = modifier) {
        AnimatedVisibility(
            visible = !isFavorite,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = notFavoriteTint
            )
        }
        AnimatedVisibility(
            visible = isFavorite,
            enter = scaleIn() + fadeIn(),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = favoriteTint
            )
        }
    }
}

@Composable
fun PageCount(
    modifier: Modifier = Modifier,
    countText: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.3f))
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_outline_image_24),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = countText,
            color = Color.White,
            modifier = Modifier.padding(start = 2.dp),
            fontSize = 12.sp
        )
    }
}

@Composable
fun RecommendCard(
    modifier: Modifier = Modifier,
    imageUrl: String,
    isFavorite: Boolean = false,
    imageContentDescription: String? = null,
    onFavoriteClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    imageRequestBuilder: ImageRequest.Builder = ImageRequest.Builder(LocalContext.current).crossfade(true),
    favoriteTint: Color = Color.Red,
    notFavoriteTint: Color = LocalContentColor.current,
    extraContent: (@Composable ColumnScope.() -> Unit)? = null,
    content: (@Composable BoxScope.() -> Unit)? = null
) {
    Card(
        shape = ShapeDefaults.ExtraSmall,
        modifier = onClick?.let {
            modifier.clickable(onClick = it)
        } ?: modifier,
    ) {
        Box(
            modifier = Modifier.weight(1.0f)
        ) {
            AsyncImage(
                model = imageRequestBuilder
                    .data(imageUrl)
                    .build(),
                contentDescription = imageContentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            content?.invoke(this)
            FavoriteIcon(
                isFavorite = isFavorite,
                favoriteTint = favoriteTint,
                notFavoriteTint = notFavoriteTint,
                modifier = Modifier.align(Alignment.BottomEnd)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .let { modifier ->
                        onFavoriteClick?.let {
                            modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null, // 禁用 ripple
                                onClick = it)
                        } ?: modifier
                    }
            )
        }
        extraContent?.invoke(this)
    }
}

@Composable
fun IllustRankCard(
    modifier: Modifier = Modifier,
    imageUrl: String,
    author: String,
    authorAvatar: String,
    title: String? = null,
    pageCount: Int = 1,
    isFavorite: Boolean = false,
    imageRequestBuilder: ImageRequest.Builder = ImageRequest.Builder(LocalContext.current).crossfade(true),
    avatarImageRequestBuilder: ImageRequest.Builder = ImageRequest.Builder(LocalContext.current).crossfade(true),
    imageContentDescription: String? = null,
    onFavoriteClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    IllustCard(
        modifier,
        imageUrl,
        pageCount,
        isFavorite,
        imageContentDescription,
        onFavoriteClick,
        onClick,
        imageRequestBuilder
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(Color.DarkGray.copy(alpha = 0.3f))
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .width(maxWidth - 40.dp)
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                if (title != null) {
                    Text(
                        text = title,
                        modifier = Modifier.padding(bottom = 4.dp),
                        fontSize = 14.sp,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = avatarImageRequestBuilder
                            .data(authorAvatar)
                            .build(),
                        contentDescription = imageContentDescription,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(16.dp)
                    )
                    Text(
                        text = author,
                        modifier = Modifier.padding(start = 4.dp),
                        fontSize = 10.sp,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun MangaRankCard(
    modifier: Modifier = Modifier,
    imageUrl: String,
    author: String,
    authorAvatar: String,
    title: String? = null,
    pageCount: Int = 1,
    isFavorite: Boolean = false,
    imageRequestBuilder: ImageRequest.Builder = ImageRequest.Builder(LocalContext.current).crossfade(true),
    avatarImageRequestBuilder: ImageRequest.Builder = ImageRequest.Builder(LocalContext.current).crossfade(true),
    imageContentDescription: String? = null,
    onClick: (() -> Unit)? = null,
    onFavoriteClick: (() -> Unit)? = null,
) {
    IllustRankCard(
        modifier,
        imageUrl,
        author,
        authorAvatar,
        title,
        pageCount,
        isFavorite,
        imageRequestBuilder,
        avatarImageRequestBuilder,
        imageContentDescription,
        onFavoriteClick,
        onClick
    )
}

/**
 * 插画卡片
 */
@Composable
fun IllustCard(
    modifier: Modifier = Modifier,
    imageUrl: String,
    pageCount: Int = 1,
    isFavorite: Boolean = false,
    imageContentDescription: String? = null,
    onFavoriteClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    imageRequestBuilder: ImageRequest.Builder = ImageRequest.Builder(LocalContext.current).crossfade(true),
    extraContent: (@Composable ColumnScope.() -> Unit)? = null,
    content: (@Composable BoxScope.() -> Unit)? = null
) {
    RecommendCard(
        modifier = modifier,
        imageUrl = imageUrl,
        isFavorite = isFavorite,
        imageContentDescription = imageContentDescription,
        imageRequestBuilder = imageRequestBuilder,
        onFavoriteClick = onFavoriteClick,
        onClick = onClick,
        extraContent = extraContent
    ) {
        if (pageCount > 1) {
            PageCount(
                countText = "$pageCount",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(ShapeDefaults.ExtraSmall)
            )
        }
        content?.invoke(this)
    }
}

/**
 * 漫画卡片
 */
@Composable
fun MangaCard(
    modifier: Modifier = Modifier,
    imageUrl: String,
    title: String,
    pageCount: Int = 1,
    tags: List<String>? = null,
    favoriteCount: Int = 0,
    isFavorite: Boolean = false,
    imageRequestBuilder: ImageRequest.Builder = ImageRequest.Builder(LocalContext.current).crossfade(true),
    imageContentDescription: String? = null,
    onFavoriteClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    IllustCard(
        modifier,
        imageUrl,
        pageCount,
        isFavorite,
        imageContentDescription,
        onFavoriteClick,
        onClick,
        imageRequestBuilder,
        extraContent = {
            ListTile(
                leading = {
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                subLeading = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (!tags.isNullOrEmpty()) {
                            Text(
                                text = tags.joinToString(" "),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(end = 2.dp)
                            )
                            Text(text = "$favoriteCount")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    )
}

@Composable
fun NovelRankingCard(
    modifier: Modifier = Modifier,
    imageUrl: String,
    title: String,
    author: String,
    authorAvatar: String,
    wordCount: Int = 1,
    tags: List<String>? = null,
    isFavorite: Boolean = false,
    imageContentDescription: String? = null,
    onFavoriteClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    avatarImageRequestBuilder: ImageRequest.Builder = ImageRequest.Builder(LocalContext.current).crossfade(true),
    imageRequestBuilder: ImageRequest.Builder = ImageRequest.Builder(LocalContext.current).crossfade(true)
) {
    RecommendCard(
        modifier = modifier,
        imageUrl = imageUrl,
        isFavorite = isFavorite,
        imageContentDescription = imageContentDescription,
        imageRequestBuilder = imageRequestBuilder,
        onFavoriteClick = onFavoriteClick,
        onClick = onClick,
        notFavoriteTint = Color.White
    ) {

        BoxWithConstraints(
            modifier = Modifier
                .align(Alignment.Center)
                .background(Color.DarkGray.copy(alpha = 0.4f))
                .fillMaxSize()
        ) {
            Text(
                text = "$wordCount 字",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 8.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.CenterVertically)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                if (!tags.isNullOrEmpty()) {
                    Text(
                        text = tags.joinToString(" "),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .width(maxWidth - 40.dp)
                    .align(Alignment.BottomStart)
                    .padding(bottom = 8.dp, start = 8.dp)
            ) {
                AsyncImage(
                    model = avatarImageRequestBuilder
                        .data(authorAvatar)
                        .build(),
                    contentDescription = imageContentDescription,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(ButtonDefaults.IconSize)
                )
                Text(
                    text = author,
                    modifier = Modifier.padding(start = 4.dp),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

/**
 * 小说卡片
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovelCard(
    modifier: Modifier = Modifier,
    imageUrl: String,
    title: String,
    series: String? = null,
    author: String,
    authorAvatarUrl: String,
    wordCount: Int,
    bookmarks: Int,
    isBookmark: Boolean,
    tags: List<String> = emptyList(),
    onSeriesClick: (() -> Unit)? = null,
    onBookmarkClick: () -> Unit = {},
    imageRequestBuilder: ImageRequest.Builder = ImageRequest.Builder(LocalContext.current).crossfade(true),
    avatarImageRequestBuilder: ImageRequest.Builder = ImageRequest.Builder(LocalContext.current).crossfade(true)
) {

    ElevatedCard(
        modifier = modifier
    ) {

        Row(
            modifier = Modifier.weight(1f)
                .padding(8.dp)
        ) {

            Column(
                modifier = Modifier
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
            ) {
                AsyncImage(
                    model = imageRequestBuilder.data(imageUrl).build(),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .width(96.dp)
                        .weight(1f),
                    contentDescription = null
                )
                Text(text = "$wordCount 字", style = MaterialTheme.typography.labelMedium)
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp, bottom = 8.dp, end = 8.dp, start = 4.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(imageVector = Icons.Outlined.Book, contentDescription = null, modifier = Modifier.padding(top = 2.dp))
                    Text(text = title, style = MaterialTheme.typography.titleMedium)
                }
                if (series != null) {
                    AssistChip(
                        onClick = onSeriesClick ?: {},
                        leadingIcon = {
                            Icon(imageVector = Icons.Outlined.Collections, contentDescription = null)
                        },
                        label = {
                            Text(
                                text = series,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
                if (tags.isNotEmpty()) {
                    Text(
                        text = tags.joinToString(" "),
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f).padding(top = 8.dp),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.Gray
                        )
                    )
                }
            }

        }

        Divider(
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        ListItem(
            headlineText = {
               Text(text = author)
            },
            leadingContent = {
                AsyncImage(
                    model = avatarImageRequestBuilder.data(authorAvatarUrl).build(),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(40.dp),
                    contentDescription = null
                )
            },
            trailingContent = {
                TextButton(
                    onClick = onBookmarkClick
                ) {
                    FavoriteIcon(isFavorite = isBookmark)
                    Text(text = "$bookmarks", modifier = Modifier.padding(start = 4.dp))
                }
            }
        )
    }

}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun IllustCardPreview() {

    var isFavorite by remember {
        mutableStateOf(false)
    }

    val imageUrl = "https://tse4-mm.cn.bing.net/th/id/OIP-C.P5Y9Ph3AUf7NSr9GzYDHjAHaEo?w=280&h=180&c=7&r=0&o=5&dpr=2&pid=1.7"

    Row {
        IllustCard(
            imageUrl = imageUrl,
            isFavorite = isFavorite,
            onFavoriteClick = {
                isFavorite = !isFavorite
            },
            modifier = Modifier.size(200.dp, height = 100.dp)
        )
        MangaCard(
            imageUrl = imageUrl,
            title = "如月澪",
            tags = listOf("#CG", "#五彩斑斓的世界"),
            isFavorite = isFavorite,
            onFavoriteClick = {
                isFavorite = !isFavorite
            },
            modifier = Modifier.size(200.dp, height = 200.dp)
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun IllustRankCardPreview() {

    var isFavorite by remember {
        mutableStateOf(false)
    }

    val imageUrl = "https://tse4-mm.cn.bing.net/th/id/OIP-C.P5Y9Ph3AUf7NSr9GzYDHjAHaEo?w=280&h=180&c=7&r=0&o=5&dpr=2&pid=1.7"

    Row {
        IllustRankCard(
            imageUrl = imageUrl,
            isFavorite = isFavorite,
            onFavoriteClick = {
                isFavorite = !isFavorite
            },
            modifier = Modifier.size(400.dp, height = 400.dp),
            author = "二阶堂梦月",
            authorAvatar = imageUrl,
            title = "如月澪"
        )
    }
}

@Preview
@Composable
fun NovelRankingCardPreview() {
    var isFavorite by remember {
        mutableStateOf(false)
    }

    val imageUrl = "https://tse4-mm.cn.bing.net/th/id/OIP-C.P5Y9Ph3AUf7NSr9GzYDHjAHaEo?w=280&h=180&c=7&r=0&o=5&dpr=2&pid=1.7"

    Row {
        NovelRankingCard(
            imageUrl = imageUrl,
            isFavorite = isFavorite,
            onFavoriteClick = {
                isFavorite = !isFavorite
            },
            modifier = Modifier.size(200.dp, height = 200.dp),
            author = "二阶堂梦月",
            authorAvatar = imageUrl,
            title = "很长很长的小说名字".repeat(2),
            tags = listOf("#测试tag", "#测试tag", "#测试tag", "#测试tag", "#测试tag", "#测试tag", "#测试tag", "#测试tag", "#测试tag", "#测试tag"),
            wordCount = 10000
        )
    }
}

@Preview
@Composable
fun NovelCardPreview() {
    var isBookmark by remember {
        mutableStateOf(false)
    }

    val imageUrl = "https://tse4-mm.cn.bing.net/th/id/OIP-C.P5Y9Ph3AUf7NSr9GzYDHjAHaEo?w=280&h=180&c=7&r=0&o=5&dpr=2&pid=1.7"
    NovelCard(
        imageUrl = imageUrl,
        title = "测试Title",
        series = "这是一个测试系列",
        author = "二阶堂梦月",
        authorAvatarUrl = imageUrl,
        wordCount = 10000,
        bookmarks = 114514,
        tags = (1..20).map {
            "测试 Tag"
        },
        isBookmark = isBookmark,
        onBookmarkClick = {
             isBookmark = !isBookmark
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(232.dp)
    )
}