package fan.yumetsuki.yumepixiv.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import fan.yumetsuki.yumepixiv.ui.screen.login.login
import fan.yumetsuki.yumepixiv.ui.screen.login.loginScreen
import fan.yumetsuki.yumepixiv.ui.screen.main.mainScreen


@Composable
fun YumePixivApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = login.route) {
        loginScreen()
        mainScreen()
    }

}