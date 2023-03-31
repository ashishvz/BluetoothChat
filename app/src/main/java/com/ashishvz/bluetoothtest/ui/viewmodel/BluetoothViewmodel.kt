package com.ashishvz.bluetoothtest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashishvz.bluetoothtest.data.BluetoothController
import com.ashishvz.bluetoothtest.ui.uiState.BluetoothUiSate
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class BluetoothViewmodel(
    private val bluetoothController: BluetoothController
) : ViewModel() {

    private val _uiSate = MutableStateFlow(BluetoothUiSate())
    val uiState: Flow<BluetoothUiSate> get() = _uiSate.asStateFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        coroutineContext.cancel(CancellationException(throwable.toString()))
    }

    /** Collects the list of paired devices from BluetoothController */
    fun getPairedDevices() {
        viewModelScope.launch(coroutineExceptionHandler) {
            _uiSate.update { it.copy(isLoading = true, pairedDevices = emptyList()) }
            bluetoothController.pairedDevices().collect { deviceList->
                _uiSate.update {
                    it.copy(pairedDevices = deviceList, isLoading = false)
                }
            }
        }
    }

    /** Search Query for searching the paired device list
     * @param searchQuery */
    fun setSearchQuery(searchQuery: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            _uiSate.update {
                it.copy(searchQuery = searchQuery)
            }
        }
    }

    /** Checking the if bluetooth is enabled ot not */
    suspend fun checkBluetoothConnection(): Flow<Boolean> {
        return bluetoothController.checkBluetoothConnectionStatus()
    }
}