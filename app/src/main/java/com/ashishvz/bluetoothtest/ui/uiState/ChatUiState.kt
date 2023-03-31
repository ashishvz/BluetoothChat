package com.ashishvz.bluetoothtest.ui.uiState

import com.ashishvz.bluetoothtest.data.model.BluetoothDevice

data class ChatUiState(
    val selectedDevice: BluetoothDevice? = null,
    val isConnectedToServer: Boolean = false,
    val isLoading: Boolean = true
)