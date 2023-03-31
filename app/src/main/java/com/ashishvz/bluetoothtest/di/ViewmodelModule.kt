package com.ashishvz.bluetoothtest.di

import com.ashishvz.bluetoothtest.ui.viewmodel.BluetoothViewmodel
import com.ashishvz.bluetoothtest.ui.viewmodel.ChatViewmodel
import org.koin.dsl.module

val viewModelModule = module {
    factory {
         BluetoothViewmodel(get())
    }
    factory {
        ChatViewmodel(get())
    }
}