package fan.yumetsuki.yumepixiv.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun NavigationDrawerItemHeadline(
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    headline: @Composable () -> Unit
) {

    Surface(
        modifier = modifier
            .height(56.dp),
        shape = shape
    ) {
        Row(
            modifier = Modifier
                .padding(start = 16.dp, end = 24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.titleSmall, content = headline)
        }
    }

}