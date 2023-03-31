package com.ashishvz.bluetoothtest.data

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.ashishvz.bluetoothtest.data.model.BluetoothDevice
import com.ashishvz.bluetoothtest.utility.Constants
import com.ashishvz.bluetoothtest.utility.NotifyUtils
import com.ashishvz.bluetoothtest.utility.toBluetoothDevices
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.InputStream
import java.io.OutputStream
import java.util.*

/** BluetoothControllerImpl class handles all the tasks related to bluetooth operations */
@SuppressLint("MissingPermission")
class BluetoothControllerImpl(
    private val context: Context
) : BluetoothController {


    private lateinit var socket: BluetoothSocket
    private lateinit var inputStream: InputStream
    private lateinit var outputStream: OutputStream
    private val androidBluetoothDevice: MutableList<android.bluetooth.BluetoothDevice> = mutableListOf()

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }

    private val bluetoothAdapter by lazy {
        bluetoothManager.adapter
    }

    private val _pairedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    override val pairedDevices = _pairedDevices.asStateFlow()

    override fun pairedDevices(): Flow<List<BluetoothDevice>> {
        return flow {
            if (!bluetoothAdapter.isEnabled) {
                NotifyUtils.notifyBluetoothNotEnabled(context)
                return@flow
            }
            androidBluetoothDevice.clear()
            val deviceList = bluetoothAdapter?.bondedDevices?.map {
                androidBluetoothDevice.add(it)
                it.toBluetoothDevices()
            } ?: emptyList()
            NotifyUtils.notifyPairedDeviceFound(context, deviceList.size)
            emit(deviceList)
        }
    }

    override fun connectToDevice(bluetoothDevice: BluetoothDevice): Flow<Boolean> {
        return flow {
            val device = androidBluetoothDevice.first { it.name == bluetoothDevice.deviceName }
            socket =
                device.createRfcommSocketToServiceRecord(UUID.fromString(Constants.SERVICE_UUID))
            try {
                socket.connect()
                inputStream = socket.inputStream
                outputStream = socket.outputStream
                emit(true)
            } catch (e: Exception) {
                e.printStackTrace()
                emit(false)
            }
        }.catch { it.printStackTrace() }
    }

    override fun sendMessage(data: String) {
        if (!bluetoothAdapter.isEnabled) {
            NotifyUtils.notifyBluetoothNotEnabled(context)
            return
        }
        if (!socket.isConnected) {
            NotifyUtils.notifyServerDisconnected(context)
            return
        }
        try { outputStream.write(data.toByteArray()) }
        catch (e: java.lang.Exception) {
            NotifyUtils.notifyServerDisconnected(context)
            socket.close()
            e.printStackTrace()
        }
    }

    override suspend fun checkSocketConnectionStatus(): Flow<Boolean> {
        return flow {
            while (true) {
                if (hasPermission(Manifest.permission.BLUETOOTH_CONNECT) || Build.VERSION.SDK_INT < Build.VERSION_CODES.S)
                    emit(socket.isConnected)
                else
                    emit(false)
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun checkBluetoothConnectionStatus(): Flow<Boolean> {
        return flow {
            while (true) {
                if(hasPermission(Manifest.permission.BLUETOOTH_SCAN) || Build.VERSION.SDK_INT < Build.VERSION_CODES.S)
                    emit(bluetoothAdapter.isEnabled)
                else
                    emit(false)
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun receiveMessages(): Flow<String> {
        return flow {
            if (!bluetoothAdapter.isEnabled)
                return@flow
            if (!socket.isConnected)
                return@flow
            val buffer = ByteArray(1024)
            while (socket.isConnected) {
                emit(String(buffer, 0, inputStream.read(buffer)))
            }
        }.flowOn(Dispatchers.IO).catch { it.printStackTrace() }
    }

    override fun releaseConnection() {
        socket.close()
    }

    private fun hasPermission(permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }
}