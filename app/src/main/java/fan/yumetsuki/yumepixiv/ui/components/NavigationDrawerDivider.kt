package fan.yumetsuki.yumepixiv.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NavigationDrawerDivider(
    modifier: Modifier = Modifier
) {

    Divider(
        modifier = modifier.padding(horizontal = 16.dp)
    )

}