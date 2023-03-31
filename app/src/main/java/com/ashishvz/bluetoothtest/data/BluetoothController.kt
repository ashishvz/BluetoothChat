package com.ashishvz.bluetoothtest.data

import com.ashishvz.bluetoothtest.data.model.BluetoothDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothController {
    val pairedDevices: StateFlow<List<BluetoothDevice>>
    fun pairedDevices(): Flow<List<BluetoothDevice>>
    suspend fun checkSocketConnectionStatus(): Flow<Boolean>
    suspend fun checkBluetoothConnectionStatus(): Flow<Boolean>
    fun connectToDevice(bluetoothDevice: BluetoothDevice): Flow<Boolean>
    fun sendMessage(data: String)
    fun receiveMessages(): Flow<String>
    fun releaseConnection()
}