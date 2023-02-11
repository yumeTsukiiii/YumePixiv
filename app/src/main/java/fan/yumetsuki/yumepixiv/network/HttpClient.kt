package fan.yumetsuki.yumepixiv.network

import fan.yumetsuki.yumepixiv.network.interceptor.HashInterceptor
import fan.yumetsuki.yumepixiv.network.interceptor.TokenInterceptor
import fan.yumetsuki.yumepixiv.data.AppRepository
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object PixivHttpHeaders {

    const val AppOs = "App-OS"

    const val AppOsVersion = "App-OS-Version"

    const val AppVersion = "App-Version"

    const val XClientHash = "X-Client-Hash"

    const val XClientTime = "X-Client-Time"

}

object PixivHosts {

    const val AppApi = "app-api.pixiv.net"

    const val OAuth = "oauth.secure.pixiv.net"

}

object PixivBaseUrls {

    const val OAuth = "https://${PixivHosts.OAuth}/"

    const val AppApiV1 = "https://${PixivHosts.AppApi}/"

}

val authWhiteUrlParts = listOf("walkthrough")

fun <T: HttpClientEngineConfig> HttpClientConfig<T>.installJson() {
    install(ContentNegotiation) {
        json(
            Json {
                encodeDefaults = true
                isLenient = true
                allowSpecialFloatingPointValues = true
                allowStructuredMapKeys = true
                prettyPrint = false
                useArrayPolymorphism = false
                ignoreUnknownKeys = true
            }
        )
    }
}

fun defaultHeader(host: String? = null): Map<String, String> = mapOf(
    PixivHttpHeaders.AppOs to "android",
    PixivHttpHeaders.AppOsVersion to "6.0.1", // TODO 看是否动态获取 AppOsVersion
    PixivHttpHeaders.AppVersion to "6.68.0",
    HttpHeaders.AcceptLanguage to "zh_CN", // TODO 国际化
    HttpHeaders.UserAgent to "PixivAndroidApp/6.68.0 (Android 6.0.1; MuMu)", // TODO 动态获取 AppOsVersion / 设备机型
    HttpHeaders.ContentType to "${ContentType.Application.Json};charset=UTF-8",
    // HttpHeaders.Connection to "Keep-Alive", // TODO 看是否有必要
).toMutableMap().apply {
    if (host != null) {
        put(HttpHeaders.Host, host)
    }
}

fun <T: HttpClientEngineConfig> HttpClientConfig<T>.installDefaultRequest(baseUrl: String, host: String) {
    install(DefaultRequest) {
        url(baseUrl)
        defaultHeader(host).forEach { (key, value) ->
            header(key, value)
        }
    }
}

fun hashInterceptor(): HashInterceptor {
    return HashInterceptor()
}

fun tokenInterceptor(
    appRepository: AppRepository
): TokenInterceptor {
    return TokenInterceptor(appRepository, authWhiteUrlParts)
}

fun oauthClient(
    hashInterceptor: HashInterceptor
): HttpClient {
    return HttpClient(Android) {
        installJson()
        installDefaultRequest(PixivBaseUrls.OAuth, PixivHosts.OAuth)
    }.apply {
        plugin(HttpSend).intercept(hashInterceptor)
    }
}

fun appApiHttpClient(
    hashInterceptor: HashInterceptor,
    tokenInterceptor: TokenInterceptor
): HttpClient {
    return HttpClient(Android) {
        installJson()
        installDefaultRequest(PixivBaseUrls.AppApiV1, PixivHosts.AppApi)
        install(HttpTimeout) {
            requestTimeoutMillis = 10000
        }
    }.apply {
        plugin(HttpSend).apply {
            intercept(tokenInterceptor)
            intercept(hashInterceptor)
        }
    }
}