package fan.yumetsuki.yumepixiv.ui.screen

import android.content.Intent
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.security.MessageDigest
import java.security.SecureRandom

fun generateCodeVerifier(): String {
    val secureRandom = SecureRandom()
    val codeVerifier = ByteArray(32)
    secureRandom.nextBytes(codeVerifier)
    return Base64.encodeToString(
        codeVerifier,
        Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
    )
}

fun generateCodeChallenge(codeVerifier: String): String {
    val bytes: ByteArray = codeVerifier.toByteArray(Charsets.US_ASCII)
    val messageDigest = MessageDigest.getInstance("SHA-256")
    messageDigest.update(bytes, 0, bytes.size)
    val digest: ByteArray = messageDigest.digest()
    return Base64.encodeToString(digest, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
}

@Composable
fun LoginScreen() {

    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {

        TextButton(onClick = {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://app-api.pixiv.net/web/v1/login?code_challenge=${
                        generateCodeChallenge(generateCodeVerifier())
                    }&code_challenge_method=S256&client=pixiv-android")
                )
            )
        }) {
            Text(text = "登录")
        }

    }

}