package fan.yumetsuki.yumepixiv.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fan.yumetsuki.yumepixiv.api.PixivRecommendApi
import fan.yumetsuki.yumepixiv.api.appApiHttpClient
import fan.yumetsuki.yumepixiv.api.impl.KtorPixivRecommendApi
import fan.yumetsuki.yumepixiv.api.oauthClient
import io.ktor.client.*
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    abstract fun bindPixivRecommendApi(
        pixivRecommendApi: KtorPixivRecommendApi
    ): PixivRecommendApi


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
    @OAuthHttpClient
    fun provideOAuthHttpClient(): HttpClient {
        return oauthClient
    }

    @Provides
    @AppApiHttpClient
    fun provideAppApiHttpClient(): HttpClient {
        return appApiHttpClient
    }

}