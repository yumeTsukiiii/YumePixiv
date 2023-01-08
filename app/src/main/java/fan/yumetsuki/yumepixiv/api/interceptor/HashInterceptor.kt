package fan.yumetsuki.yumepixiv.api.interceptor

import android.annotation.SuppressLint
import fan.yumetsuki.yumepixiv.api.PixivHttpHeaders
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import java.math.BigInteger
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

internal const val HashSalt = "28c1fdd170a5204386cb1313c7077b34f83e4aaf4aa829ce78c231e05b0bae2c"

internal fun hash(text: String): String = BigInteger(
    1,
    MessageDigest.getInstance("MD5").digest(
        text.toByteArray(Charsets.UTF_8)
    )
).toString(16).padStart(32, '0')

@Suppress("SpellCheckingInspection")
@SuppressLint("SimpleDateFormat")
fun currentIsoDateTime(): String = SimpleDateFormat(
    // Calendar.getInstance 已经是 localTime，无需 +08:00 时区
    "yyyy-MM-dd'T'HH:mm:ss'+00:00'"
).format(
    Calendar.getInstance().time
)

fun xClientHash(dateTime: String): String = hash(HashSalt + dateTime)

class HashInterceptor : KtorHttpSendInterceptor {

    override suspend fun invoke(sender: Sender, request: HttpRequestBuilder): HttpClientCall {
        return sender.execute(
            request.apply {
                val requestTime = currentIsoDateTime()
                header(PixivHttpHeaders.XClientHash, xClientHash(requestTime))
                header(PixivHttpHeaders.XClientTime, requestTime)
            }
        )
    }

}