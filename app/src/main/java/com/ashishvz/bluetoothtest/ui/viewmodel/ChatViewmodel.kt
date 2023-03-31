package com.ashishvz.bluetoothtest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashishvz.bluetoothtest.data.BluetoothController
import com.ashishvz.bluetoothtest.data.model.BluetoothDevice
import com.ashishvz.bluetoothtest.data.model.Message
import com.ashishvz.bluetoothtest.ui.uiState.ChatUiState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class ChatViewmodel(
    private val bluetoothController: BluetoothController
) : ViewModel() {

    private val _uiSate = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> get() = _uiSate.asStateFlow()

    private val _messageListFlow = MutableSharedFlow<Message>()
    val messageListFlow: SharedFlow<Message> get() = _messageListFlow

    private val messageList: Message = Message()

    /** Coroutine exception handler to catch the
     * exception occurred in coroutines **/
    private val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        coroutineContext.cancel(CancellationException(throwable.toString()))
    }

    /** Connects to the selected device
     * @param bluetoothDevice
     * @sample connectToDevice(bluetoothDevice)
     * @return Flow<Boolean> is connected or not
     */
    fun connectToDevice(bluetoothDevice: BluetoothDevice): Flow<Boolean> {
        viewModelScope.launch { _uiSate.update {
            it.copy(isLoading = true)
        } }
        messageList.messageList.clear()
        return bluetoothController.connectToDevice(bluetoothDevice).flowOn(Dispatchers.IO)
    }

    /** Sends message to the connected device
     * @param message String property
     */
    suspend fun sendMessage(message: String) {
        messageList.messageList.add("Me: $message")
        _messageListFlow.emit(messageList)
        bluetoothController.sendMessage(message)
    }

    /** Close the socket connection after use */
    fun releaseConnection() {
        bluetoothController.releaseConnection()
    }

    /** Receive Messages from the connected server
     * and update it to UI */
    fun collectMessages() {
        viewModelScope.launch(coroutineExceptionHandler) {
            bluetoothController.receiveMessages()
                .catch {
                    it.printStackTrace()
                }
                .collect {
                    messageList.messageList.add("Server: $it")
                    _messageListFlow.emit(messageList)
                }
        }
    }

    /** Flow of bluetooth connection updated to UI
     * @return Flow<Boolean>*/
    suspend fun checkBluetoothConnection(): Flow<Boolean> {
        return bluetoothController.checkBluetoothConnectionStatus()
    }

    /** Flow of socket connection updated to UI
     *@return Flow<Boolean>*/
    suspend fun keepCheckingSocketConnection(): Flow<Boolean> {
        return bluetoothController.checkSocketConnectionStatus()
    }
}