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

fun <T: HttpClientEngineConfig> HttpClientConfig<T>.installDefaultRequest(baseUrl: String, host: String) {
    install(DefaultRequest) {
        url(baseUrl)
        // TODO 看是否动态获取 AppOsVersion
        header(PixivHttpHeaders.AppOs, "android")
        header(PixivHttpHeaders.AppOsVersion, "6.0.1")
        header(PixivHttpHeaders.AppVersion, "6.68.0")

        // 不能添加 gzip encoding，否则会 MalformedInputException
//        header(HttpHeaders.AcceptEncoding, "gzip")
        // TODO 国际化
        header(HttpHeaders.AcceptLanguage, "zh_CN")
        // TODO 动态获取 AppOsVersion / 设备机型
        header(HttpHeaders.UserAgent, "PixivAndroidApp/6.68.0 (Android 6.0.1; MuMu)")

        // TODO 看是否有必要
        header(HttpHeaders.ContentType, "${ContentType.Application.Json};charset=UTF-8")
//        header(HttpHeaders.Connection, "Keep-Alive")

        header(HttpHeaders.Host, host)
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
            requestTimeoutMillis = 5000
        }
    }.apply {
        plugin(HttpSend).apply {
            intercept(tokenInterceptor)
            intercept(hashInterceptor)
        }
    }
}