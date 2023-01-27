package fan.yumetsuki.yumepixiv.ui.components

import android.graphics.Typeface
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HeartBroken
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

@Composable
fun YumePixivTip(
    text: String,
    modifier: Modifier = Modifier,
    icon: @Composable ColumnScope.() -> Unit = {
        Icon(
            imageVector = Icons.Outlined.HeartBroken,
            contentDescription = null,
            modifier = Modifier.size(96.dp)
        )
    },
) {

    val context = LocalContext.current

    val lolitaFont = remember(context) {
        FontFamily(
            Typeface.createFromAsset(
                context.assets,
                "LOLITA.ttf"
            )
        )
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        icon()
        Text(text = text, fontFamily = lolitaFont, modifier = Modifier.padding(top = 16.dp))
    }



}