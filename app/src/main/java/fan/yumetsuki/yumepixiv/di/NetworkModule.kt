package fan.yumetsuki.yumepixiv.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fan.yumetsuki.yumepixiv.network.*
import fan.yumetsuki.yumepixiv.network.impl.KtorPixivAppApi
import fan.yumetsuki.yumepixiv.network.interceptor.HashInterceptor
import fan.yumetsuki.yumepixiv.network.interceptor.TokenInterceptor
import fan.yumetsuki.yumepixiv.data.AppRepository
import fan.yumetsuki.yumepixiv.network.impl.KtorPixivAuthApi
import io.ktor.client.*
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    abstract fun bindPixivRecommendApi(
        pixivRecommendApi: KtorPixivAppApi
    ): PixivAppApi

    @Binds
    abstract fun bindPixivAuthApi(
        pixivAuthApi: KtorPixivAuthApi
    ): PixivAuthApi
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OAuthHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppApiHttpClient

@Module
@InstallIn(SingletonComponent::class)
object KtorModule {

    @Provides
    @Singleton
    fun provideHashInterceptor(): HashInterceptor {
        return hashInterceptor()
    }

    @Provides
    @Singleton
    fun provideTokenInterceptor(
        appRepository: AppRepository
    ): TokenInterceptor {
        return tokenInterceptor(appRepository)
    }

    @Provides
    @Singleton
    @OAuthHttpClient
    fun provideOAuthHttpClient(
        hashInterceptor: HashInterceptor
    ): HttpClient {
        return oauthClient(hashInterceptor)
    }

    @Provides
    @Singleton
    @AppApiHttpClient
    fun provideAppApiHttpClient(
        hashInterceptor: HashInterceptor,
        tokenInterceptor: TokenInterceptor
    ): HttpClient {
        return appApiHttpClient(hashInterceptor, tokenInterceptor)
    }

}