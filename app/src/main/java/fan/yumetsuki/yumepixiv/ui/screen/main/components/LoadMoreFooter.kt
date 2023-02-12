package fan.yumetsuki.yumepixiv.ui.screen.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import fan.yumetsuki.yumepixiv.ui.components.LoadMore
import fan.yumetsuki.yumepixiv.utils.rememberLolitaFont

@Composable
fun LoadMoreFooter(
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    if (isError) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .then(modifier),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.errorContainer
            )
            Text(
                text = "寄！请求失败了！！！点击重试！！",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = rememberLolitaFont(),
                modifier = Modifier.alpha(0.4f)
            )
        }
    } else {
        LoadMore(modifier = Modifier.fillMaxWidth()) {
            Text(text = "加载中", fontFamily = rememberLolitaFont())
        }
    }
}