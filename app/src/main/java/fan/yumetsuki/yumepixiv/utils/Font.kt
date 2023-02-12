package fan.yumetsuki.yumepixiv.utils

import android.content.Context
import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily

@Composable
fun rememberLolitaFont(
    context: Context = LocalContext.current
): FontFamily {
    return rememberFontFamily(path = "LOLITA.ttf", context = context)
}

@Composable
fun rememberFontFamily(
    path: String,
    context: Context = LocalContext.current
): FontFamily {
    return remember(context) {
        FontFamily(
            Typeface.createFromAsset(
                context.assets,
                path
            )
        )
    }
}