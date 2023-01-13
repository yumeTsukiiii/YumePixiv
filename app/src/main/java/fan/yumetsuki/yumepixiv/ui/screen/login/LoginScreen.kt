package fan.yumetsuki.yumepixiv.ui.screen.login

import android.graphics.Typeface
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.outlined.HeartBroken
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import coil.compose.AsyncImage
import fan.yumetsuki.yumepixiv.ui.components.AutoLazyVerticalGrid
import fan.yumetsuki.yumepixiv.ui.screen.Route
import fan.yumetsuki.yumepixiv.utils.pixivImageRequestBuilder
import fan.yumetsuki.yumepixiv.viewmodels.LoginViewModel

val login = Route(
    route = "login"
)

fun NavGraphBuilder.loginScreen() {
    composable(login.route) {
        LoginScreen()
    }
}

@Suppress("unused")
fun NavController.navigateToLogin(navOptions: NavOptions? = null) {
    navigate(login.route, navOptions)
}

fun NavController.navigateToLogin(builder: NavOptionsBuilder.() -> Unit) {
    navigate(login.route, builder)
}

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    val screenState by viewModel.uiState.collectAsState()

    val lolitaFont = remember(context) {
        FontFamily(
            Typeface.createFromAsset(
                context.assets,
                "LOLITA.ttf"
            )
        )
    }

    LaunchedEffect(Unit) {
        viewModel.requestWorkThroughIllusts()
    }

    Box {

        // 背景图 walk through
        screenState.workThroughImages?.also { workThroughImages ->
            AutoLazyVerticalGrid(
                items = workThroughImages,
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize()
            ) {
                AsyncImage(
                    model = pixivImageRequestBuilder(imageUrl = this)
                        .build(),
                    modifier = Modifier.height(216.dp),
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
            }
        } ?: Column(
            modifier = Modifier.fillMaxSize()
                .align(Alignment.Center)
                .padding(top = 32.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.HeartBroken,
                contentDescription = null,
                modifier = Modifier.size(96.dp)
                    .padding(bottom = 16.dp)
            )
            Text(text = "加载出错辣！！你的网络可能寄了", fontFamily = lolitaFont)
        }

        // login 操作主区块
        Column(
            modifier = Modifier
                .background(Color.Black.copy(0.5f))
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "YumePixiv",
                style = LocalTextStyle.current.merge(MaterialTheme.typography.displayMedium)
                    .copy(
                        color = Color.White.copy(0.9f),
                        fontFamily = lolitaFont,
                        letterSpacing = 4.sp
                    ),
            )
            Button(
                onClick = {
                    viewModel.jumpToLogin(context)
                },
                modifier = Modifier
                    .widthIn(max = 456.dp)
                    .fillMaxWidth(0.7f)
                    .padding(top = 48.dp),
            ) {
                Text(text = "登录")
            }
            TextButton(
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.White.copy(0.8f)
                ),
                onClick = { /*TODO*/ }
            ) {
                Text(text = "没有账号？戳我！")
            }
        }

        // 问题反馈等副区块

        Row(
            modifier = Modifier.align(Alignment.BottomEnd)
                .padding(bottom = 8.dp, end = 8.dp)
        ) {
            TextButton(
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.White.copy(0.8f)
                ),
                onClick = { /*TODO*/ }
            ) {
                Icon(imageVector = Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                Text(
                    text = "有问题都点我这里！",
                    style = LocalTextStyle.current.merge(MaterialTheme.typography.labelLarge),
                    modifier = Modifier.padding(start = ButtonDefaults.IconSpacing)
                )
            }
        }

    }

}