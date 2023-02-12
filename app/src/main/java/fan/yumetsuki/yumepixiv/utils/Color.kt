package fan.yumetsuki.yumepixiv.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import okhttp3.internal.toHexString

fun Color.toColorHexString(includeAlpha: Boolean = false): String {
    return "#${
        toArgb().toHexString().substring(
            if (includeAlpha) 0 else 2
        )
    }"
}