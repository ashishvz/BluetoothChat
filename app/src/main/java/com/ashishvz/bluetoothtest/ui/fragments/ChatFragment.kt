package com.ashishvz.bluetoothtest.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ashishvz.bluetoothtest.R
import com.ashishvz.bluetoothtest.databinding.FragmentChatBinding
import com.ashishvz.bluetoothtest.ui.viewmodel.ChatViewmodel
import com.ashishvz.bluetoothtest.utility.NotifyUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private val chatViewmodel by viewModel<ChatViewmodel>()
    private val args: ChatFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectUiState()
        binding.deviceName.text = args.data.deviceName
        binding.apply {
            sendButton.setOnClickListener {
                lifecycleScope.launch {
                    chatViewmodel.sendMessage(messageTextInputEditText.text.toString())
                }
            }
            backButton.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    /** Collecting UI state from viewmodel */

    private fun collectUiState() {
        lifecycleScope.launch(Dispatchers.IO) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                /** First Check for bluetooth is enabled or not */
                launch(Dispatchers.IO) {
                    chatViewmodel.checkBluetoothConnection().distinctUntilChanged().collect {
                        withContext(Dispatchers.Main) {
                            /** If bluetooth is enabled then connect the device to server socket */
                            if (it) {
                                chatViewmodel.connectToDevice(args.data).distinctUntilChanged().collectLatest {
                                    launch(Dispatchers.Main) {
                                        binding.contentLoadingBar.hide()
                                        if (it) {
                                            NotifyUtils.showToast(
                                                requireContext(),
                                                "Connected Successfully!"
                                            )
                                            withContext(Dispatchers.IO) { chatViewmodel.collectMessages() }
                                        } else
                                            NotifyUtils.showToast(requireContext(), "Connection Failed!")
                                        keepOnCheckingSocketConnection()
                                    }
                                }
                            } else {
                                NotifyUtils.notifyBluetoothNotEnabled(requireContext())
                            }
                        }
                    }
                }
            }
        }

        /** Updating content loading progress bar */
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                chatViewmodel.uiState.collectLatest {
                    binding.apply {
                        contentLoadingBar.isVisible = it.isLoading
                    }
                }
            }
        }

        /** Updating the message data to UI */
        lifecycleScope.launch(Dispatchers.IO) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                chatViewmodel.messageListFlow.collectLatest {
                    binding.apply {
                        withContext(Dispatchers.Main) {
                            chatListView.adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_list_item_1,
                                it.messageList
                            )
                            messageTextInputEditText.text?.clear()
                        }
                    }
                }
            }
        }
    }

    /** Checks for socket connection frequently but
     * the value is updated only when it is changed as we have used
     * distinctUntilChanged() operator which filters the repeated values.
     * Here we are also disabling the message textInputEditText and send button
     * if socket is disconnected **/

    private fun keepOnCheckingSocketConnection() {
        lifecycleScope.launch(Dispatchers.IO) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                chatViewmodel.keepCheckingSocketConnection().distinctUntilChanged().collect { connectionStatus ->
                        /** Ui task switching the thread from IO to Main */
                        withContext(Dispatchers.Main) {
                            binding.apply {
                                isConnectedText.text = buildString {
                                    append("Is Device Connected To Server:")
                                    append(
                                        if (connectionStatus) requireContext().getString(R.string.yes) else requireContext().getString(
                                            R.string.no
                                        )
                                    )
                                }
                                contentLoadingBar.hide()
                                messageTextInputEditText.isEnabled = connectionStatus
                                sendButton.isEnabled = connectionStatus
                            }
                        }
                    }
            }
        }
    }

    /** Stopping or releasing socket connection after
     * the fragment is destroyed */
    override fun onDestroy() {
        super.onDestroy()
        chatViewmodel.releaseConnection()
    }
}