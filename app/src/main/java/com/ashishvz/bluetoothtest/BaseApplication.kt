package com.ashishvz.bluetoothtest

import android.app.Application
import com.ashishvz.bluetoothtest.di.bluetoothModule
import com.ashishvz.bluetoothtest.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class BaseApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(listOf(bluetoothModule, viewModelModule))
            androidContext(applicationContext)
            androidLogger(Level.DEBUG)
        }
    }
}