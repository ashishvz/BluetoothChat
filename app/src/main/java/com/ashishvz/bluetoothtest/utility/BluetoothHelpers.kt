package com.ashishvz.bluetoothtest.utility

import android.annotation.SuppressLint
import com.ashishvz.bluetoothtest.data.model.BluetoothDevice

/** Mapper function to map android.bluetooth.BluetoothDevice
 * to our model com.ashishvz.bluetoothtest.data.model.BluetoothDevice */

@SuppressLint("MissingPermission")
fun android.bluetooth.BluetoothDevice.toBluetoothDevices(): BluetoothDevice {
    return BluetoothDevice(deviceName = name, deviceMacAddress = address)
}