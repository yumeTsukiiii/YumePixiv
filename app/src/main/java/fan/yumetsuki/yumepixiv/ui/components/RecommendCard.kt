package fan.yumetsuki.yumepixiv.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import fan.yumetsuki.yumepixiv.R

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FavoriteIcon(
    modifier: Modifier = Modifier,
    isFavorite: Boolean
) {
    Box(modifier = modifier) {
        AnimatedVisibility(
            visible = !isFavorite,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = null)
        }
        AnimatedVisibility(
            visible = isFavorite,
            enter = scaleIn() + fadeIn(),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = Color.Red)
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
    extraContent: (@Composable ColumnScope.() -> Unit)? = null,
    content: (@Composable BoxScope.() -> Unit)? = null,
) {
    Card(
        shape = ShapeDefaults.ExtraSmall,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.weight(1.0f)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = imageContentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            content?.invoke(this)
            FavoriteIcon(
                isFavorite = isFavorite,
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
    imageContentDescription: String? = null,
    onFavoriteClick: (() -> Unit)? = null,
) {
    IllustCard(
        modifier, imageUrl, pageCount, isFavorite, imageContentDescription, onFavoriteClick
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .align(Alignment.BottomCenter)
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
                        color = Color.White
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(authorAvatar)
                            .crossfade(true)
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
                        color = Color.White
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
    imageContentDescription: String? = null,
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
        imageContentDescription,
        onFavoriteClick
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
    extraContent: (@Composable ColumnScope.() -> Unit)? = null,
    content: (@Composable BoxScope.() -> Unit)? = null
) {
    RecommendCard(
        modifier = modifier,
        imageUrl = imageUrl,
        isFavorite = isFavorite,
        imageContentDescription = imageContentDescription,
        onFavoriteClick = onFavoriteClick,
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
    imageContentDescription: String? = null,
    onFavoriteClick: (() -> Unit)? = null,
) {
    IllustCard(
        modifier, imageUrl, pageCount, isFavorite, imageContentDescription, onFavoriteClick,
        extraContent = {
            ListTile(
                leading = {
                    Text(
                        text = title
                    )
                },
                subLeading = {
                    Column {
                        if (!tags.isNullOrEmpty()) {
                            Text(text = tags.joinToString("  "))
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

/**
 * 小说卡片
 */
@Composable
fun NovelCard() {

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