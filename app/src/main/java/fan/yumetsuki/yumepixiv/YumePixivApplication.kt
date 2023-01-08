package fan.yumetsuki.yumepixiv

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import fan.yumetsuki.yumepixiv.data.AppRepository

@HiltAndroidApp
class YumePixivApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        internalAppRepository = AppRepository(
            sharedPreferences = getSharedPreferences(AppStorage, Context.MODE_PRIVATE)
        )
    }

    companion object {

        private const val AppStorage = "AppStorage"

        private lateinit var internalAppRepository: AppRepository

        val appRepository: AppRepository
            get() = internalAppRepository

    }
}