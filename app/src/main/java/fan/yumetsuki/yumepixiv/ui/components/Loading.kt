package fan.yumetsuki.yumepixiv.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoadMore(
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)? = null
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.padding(vertical = 16.dp, horizontal = 8.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            strokeWidth = 2.dp
        )
        if (content != null) {
            Box(modifier = Modifier.padding(start = 16.dp)) {
                content()
            }
        }
    }

}