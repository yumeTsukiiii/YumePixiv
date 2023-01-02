package fan.yumetsuki.yumepixiv.ui.screen

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

@Suppress("MemberVisibilityCanBePrivate")
sealed class Route(
    val route: String,
    val label: String,
    val icon: ImageVector?,
    val content: NavGraphBuilder.(navController: NavController) -> Unit,
)