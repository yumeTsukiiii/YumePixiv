package fan.yumetsuki.yumepixiv.ui.screen.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import fan.yumetsuki.yumepixiv.ui.screen.Route
import fan.yumetsuki.yumepixiv.viewmodels.LoginViewModel

const val Via = "via"

const val Token = "token"

val login = Route(
    route = "login?code={${Token}}"
)

fun NavGraphBuilder.loginScreen() {
    composable(
        login.route,
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "pixiv://account/login?code={${Token}}&via={${Via}}"
            }
        )
    ) { navBackStackEntry ->
        LoginScreen(
            accessToken = navBackStackEntry.arguments?.getString(Token)
        )
    }
}

@Composable
fun LoginScreen(
    accessToken: String? = null,
    viewModel: LoginViewModel = hiltViewModel()
) {

    val screenState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(accessToken) {
        if (accessToken != null) {
            viewModel.storeNewLoginToken(accessToken)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        if (accessToken == null) {
            TextButton(onClick = {
                viewModel.jumpToLogin(context)
            }) {
                Text(text = "登录")
            }
        } else {
            if (screenState.isRefreshingToken) {
                CircularProgressIndicator()
            } else {
                if (screenState.isTokenUpdated) {
                    Text(text = "Token 刷新完毕")
                } else {
                    Text(text = "登录成功..App 就绪")
                }
            }
        }

    }

}