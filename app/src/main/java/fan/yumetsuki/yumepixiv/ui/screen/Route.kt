package fan.yumetsuki.yumepixiv.ui.screen

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

@Suppress("MemberVisibilityCanBePrivate")
class Route(
    val route: String,
    val label: String? = null,
    val icon: ImageVector? = null,
)