package fan.yumetsuki.yumepixiv.network.interceptor

import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*

typealias KtorHttpSendInterceptor = suspend Sender.(HttpRequestBuilder) -> HttpClientCall