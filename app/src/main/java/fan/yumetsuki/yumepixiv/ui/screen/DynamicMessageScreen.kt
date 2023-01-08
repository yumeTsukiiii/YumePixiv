package fan.yumetsuki.yumepixiv.ui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import fan.yumetsuki.yumepixiv.ui.Route

val dynamicMessage = Route(
    route = "dynamicMessage",
    label = "动态",
    icon = Icons.Default.Star
)

fun NavGraphBuilder.dynamicMessageScreen() {
    composable(dynamicMessage.route) {
        DynamicMessage()
    }
}

@Composable
fun DynamicMessage() {
    Text(text = "2333")
}