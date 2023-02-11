package fan.yumetsuki.yumepixiv.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import fan.yumetsuki.yumepixiv.ui.screen.login.loginScreen
import fan.yumetsuki.yumepixiv.ui.screen.main.mainScreen
import fan.yumetsuki.yumepixiv.ui.screen.web.webViewScreen
import fan.yumetsuki.yumepixiv.ui.screen.welcome.welcome
import fan.yumetsuki.yumepixiv.ui.screen.welcome.welcomeScreen


@Composable
fun YumePixivApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = welcome.route) {
        welcomeScreen(navController)
        loginScreen()
        mainScreen(navController = navController)
        webViewScreen()
    }

}