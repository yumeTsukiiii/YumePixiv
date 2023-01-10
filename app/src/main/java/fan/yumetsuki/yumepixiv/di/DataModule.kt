package fan.yumetsuki.yumepixiv.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fan.yumetsuki.yumepixiv.data.AppRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideAppRepository(
        application: Application
    ): AppRepository {
        return AppRepository(
            application.getSharedPreferences("AppStorage", Context.MODE_PRIVATE)
        )
    }

}