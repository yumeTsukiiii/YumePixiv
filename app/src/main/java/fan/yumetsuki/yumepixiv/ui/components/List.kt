package fan.yumetsuki.yumepixiv.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ListTile(
    modifier: Modifier = Modifier,
    leading: @Composable () -> Unit,
    subLeading: (@Composable () -> Unit)? = null,
    tail: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1.0f)
        ) {
            CompositionLocalProvider(
                LocalTextStyle provides LocalTextStyle.current.merge(MaterialTheme.typography.labelLarge),
                LocalContentColor provides Color.DarkGray
            ) {
                leading()
            }
            if (subLeading != null) {
                CompositionLocalProvider(
                    LocalTextStyle provides LocalTextStyle.current.merge(MaterialTheme.typography.labelSmall),
                    LocalContentColor provides Color.Gray
                ) {
                    subLeading()
                }
            }
        }
        tail?.invoke()
    }
}