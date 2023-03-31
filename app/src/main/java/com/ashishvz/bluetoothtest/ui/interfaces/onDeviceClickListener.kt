package com.ashishvz.bluetoothtest.ui.interfaces

import com.ashishvz.bluetoothtest.data.model.BluetoothDevice

interface OnDeviceClickListener {
    fun onClick(bluetoothDevice: BluetoothDevice)
}