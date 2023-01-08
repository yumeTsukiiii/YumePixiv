package fan.yumetsuki.yumepixiv.api

import android.annotation.SuppressLint
import fan.yumetsuki.yumepixiv.YumePixivApplication
import fan.yumetsuki.yumepixiv.api.interceptor.HashInterceptor
import fan.yumetsuki.yumepixiv.api.interceptor.KtorHttpSendInterceptor
import fan.yumetsuki.yumepixiv.api.interceptor.TokenInterceptor
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import java.math.BigInteger
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Calendar

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

    const val OAuth = "https://${PixivHosts.OAuth}/auth/token/"

    const val AppApiV1 = "https://${PixivHosts.AppApi}/v1/"

}

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

val hashInterceptor: KtorHttpSendInterceptor
    get() = HashInterceptor()

val tokenInterceptor: KtorHttpSendInterceptor
    get() = TokenInterceptor(
        YumePixivApplication.appRepository
    )

val oauthClient
    get() = HttpClient(Android) {
        installJson()
        installDefaultRequest(PixivBaseUrls.OAuth, PixivHosts.OAuth)
    }.apply {
        plugin(HttpSend).intercept(hashInterceptor)
    }

val appApiHttpClient
    get() = HttpClient(Android) {
        installJson()
        installDefaultRequest(PixivBaseUrls.AppApiV1, PixivHosts.AppApi)
        install(Auth) {
            bearer {
                loadTokens {
                    // TODO 获取 access token 和 refresh token，不是很标准，不用 Auth 模块了，直接拦截器
                    BearerTokens(
                        "",
                        ""
                    )
                }
            }
        }
    }.apply {
        plugin(HttpSend).apply {
            intercept(hashInterceptor)
            intercept(tokenInterceptor)
        }
    }