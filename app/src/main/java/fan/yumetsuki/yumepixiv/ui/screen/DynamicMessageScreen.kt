package fan.yumetsuki.yumepixiv.ui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

object DynamicMessage: Route(
    route = "dynamicMessage",
    label = "动态",
    icon = Icons.Default.Star,
    content = { dynamicMessageScreen() }
)

fun NavGraphBuilder.dynamicMessageScreen() {
    composable(DynamicMessage.route) {
        DynamicMessage()
    }
}

@Composable
fun DynamicMessage() {
    Text(text = "2333")
}