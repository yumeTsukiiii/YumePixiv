package fan.yumetsuki.yumepixiv.ui.screen.welcome

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import fan.yumetsuki.yumepixiv.ui.screen.Route
import fan.yumetsuki.yumepixiv.ui.screen.login.navigateToLogin
import fan.yumetsuki.yumepixiv.ui.screen.main.navigateToMain
import fan.yumetsuki.yumepixiv.viewmodels.WelcomeViewModel

const val Via = "via"

const val Token = "token"

val welcome = Route(
    route = "welcome?code={${Token}}"
)

fun NavGraphBuilder.welcomeScreen(
    rootNavController: NavController
) {
    composable(
        welcome.route,
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "pixiv://account/login?code={${Token}}&via={${Via}}"
            }
        )
    ) { navBackStackEntry ->
        WelcomeScreen(
            rootNavController = rootNavController,
            code = navBackStackEntry.arguments?.getString(Token)
        )
    }
}

@Composable
fun WelcomeScreen(
    code: String? = null,
    rootNavController: NavController,
    welcomeViewModel: WelcomeViewModel = hiltViewModel()
) {

    val screenState by welcomeViewModel.uiState.collectAsState()

    LaunchedEffect(code) {
        if (code != null) {
            welcomeViewModel.storeNewLoginToken(code)
        } else {
            welcomeViewModel.checkTokenExisted()
        }
    }

    LaunchedEffect(screenState.isRefreshingToken, screenState.isTokenExisted) {
        if (!screenState.isRefreshingToken) {
            if (screenState.isTokenExisted) {
                rootNavController.navigateToMain {
                    popUpTo(route = welcome.route) {
                        inclusive = true
                    }
                }
            } else {
                rootNavController.navigateToLogin {
                    popUpTo(route = welcome.route) {
                        inclusive = true
                    }
                }
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            // TODO 换一张好看的图片，本地内置就行（
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

}