package fan.yumetsuki.yumepixiv.utils

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom

fun generateCodeVerifier(
    secureRandom: SecureRandom = SecureRandom()
): String {
    val codeVerifier = ByteArray(32)
    secureRandom.nextBytes(codeVerifier)
    return Base64.encodeToString(
        codeVerifier,
        Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
    )
}

fun generateCodeChallenge(
    codeVerifier: String,
    messageDigest: MessageDigest = MessageDigest.getInstance("SHA-256")
): String {
    val bytes: ByteArray = codeVerifier.toByteArray(Charsets.US_ASCII)
    messageDigest.update(bytes, 0, bytes.size)
    val digest: ByteArray = messageDigest.digest()
    return Base64.encodeToString(digest, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
}