package com.example.videorewardingsystem

import android.app.Application
import com.example.videorewardingsystem.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class VideoRewardingApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@VideoRewardingApp)
            modules(appModule)
        }
    }
}
