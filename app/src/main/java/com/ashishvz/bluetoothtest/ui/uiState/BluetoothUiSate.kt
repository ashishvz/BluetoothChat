package com.ashishvz.bluetoothtest.ui.uiState

import com.ashishvz.bluetoothtest.data.model.BluetoothDevice

data class BluetoothUiSate(
    val pairedDevices: List<BluetoothDevice> = emptyList(),
    val searchQuery: String? = null,
    val isLoading: Boolean = true
)