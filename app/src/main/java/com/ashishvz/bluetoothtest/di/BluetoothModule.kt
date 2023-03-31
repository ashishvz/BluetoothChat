package com.ashishvz.bluetoothtest.di

import com.ashishvz.bluetoothtest.data.BluetoothController
import com.ashishvz.bluetoothtest.data.BluetoothControllerImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val bluetoothModule = module {
    single<BluetoothController> {
        BluetoothControllerImpl(androidApplication())
    }
}