package fan.yumetsuki.yumepixiv.utils

import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.charsets.*
import io.ktor.utils.io.core.*
import java.nio.charset.Charset
import kotlin.text.Charsets

fun ByteArray.asText(httpResponse: HttpResponse, fallbackCharset: Charset = Charsets.UTF_8): String {
    val originCharset = httpResponse.charset() ?: fallbackCharset
    val decoder = originCharset.newDecoder()
    val input = ByteReadPacket(this)

    return decoder.decode(input)
}