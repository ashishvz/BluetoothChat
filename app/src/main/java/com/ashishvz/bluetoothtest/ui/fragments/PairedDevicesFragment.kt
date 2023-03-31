package com.ashishvz.bluetoothtest.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashishvz.bluetoothtest.R
import com.ashishvz.bluetoothtest.data.model.BluetoothDevice
import com.ashishvz.bluetoothtest.databinding.FragmentPairedDevicesBinding
import com.ashishvz.bluetoothtest.ui.adapter.DeviceAdapter
import com.ashishvz.bluetoothtest.ui.interfaces.OnDeviceClickListener
import com.ashishvz.bluetoothtest.ui.viewmodel.BluetoothViewmodel
import com.ashishvz.bluetoothtest.utility.NotifyUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PairedDevicesFragment : Fragment(), OnDeviceClickListener {

    private lateinit var binding: FragmentPairedDevicesBinding
    private val bluetoothViewmodel by viewModel<BluetoothViewmodel>()
    private lateinit var deviceAdapter: DeviceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_paired_devices, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectUiState()
        deviceAdapter = DeviceAdapter(this)
        binding.apply {
            deviceRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            deviceRecyclerView.adapter = deviceAdapter
            searchTextInput.doOnTextChanged { text, _, _, _ ->
                bluetoothViewmodel.setSearchQuery(text.toString())
            }
            bluetoothSearchImage.setOnClickListener {
                clearIcon.performClick()
                bluetoothViewmodel.getPairedDevices()
            }
            clearIcon.setOnClickListener {
                bluetoothViewmodel.setSearchQuery("")
                searchTextInput.setText("")
            }
        }
    }

    /** Collects UI State from BluetoothViewmodel
     * */
    private fun collectUiState() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                bluetoothViewmodel.checkBluetoothConnection().distinctUntilChanged().collect {
                    if (it) {
                        launch {
                            bluetoothViewmodel.uiState.collectLatest {
                                binding.apply {
                                    deviceAdapter.updateDeviceList(
                                        it.pairedDevices.filter { pairedDevice ->
                                            if (it.searchQuery.isNullOrEmpty())
                                                return@filter true
                                            pairedDevice.deviceName?.contains(
                                                it.searchQuery.toString(),
                                                true
                                            )!!
                                        }
                                    )
                                    contentLoadingBar.isVisible = it.isLoading
                                }
                            }
                        }
                        bluetoothViewmodel.getPairedDevices()
                    } else {
                        NotifyUtils.notifyBluetoothNotEnabledOrPermissionDenied(requireContext())
                    }
                }
            }
        }
    }

    /** On item click from recycler view navigating to
     * the ChatFragment for connecting to server */
    override fun onClick(bluetoothDevice: BluetoothDevice) {
        findNavController().navigate(
            PairedDevicesFragmentDirections.actionPairedDevicesFragmentToChatFragment(
                bluetoothDevice
            )
        )
    }
}