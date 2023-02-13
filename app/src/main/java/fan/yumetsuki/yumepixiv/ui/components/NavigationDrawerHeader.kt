package fan.yumetsuki.yumepixiv.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawerHeader(
    modifier: Modifier = Modifier,
    leadingContent: @Composable (() -> Unit)? = null,
    headlineText: @Composable () -> Unit
) {

    Box(
        modifier = modifier.heightIn(min = 96.dp)
    ) {
        ListItem(
            leadingContent = leadingContent,
            headlineText = {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.titleLarge, content = headlineText
                )
            },
            modifier = Modifier.align(Alignment.Center).fillMaxWidth()
        )
    }

}