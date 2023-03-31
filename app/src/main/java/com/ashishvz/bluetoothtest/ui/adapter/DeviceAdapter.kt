package com.ashishvz.bluetoothtest.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ashishvz.bluetoothtest.R
import com.ashishvz.bluetoothtest.data.model.BluetoothDevice
import com.ashishvz.bluetoothtest.databinding.BluetoothDeviceCardBinding
import com.ashishvz.bluetoothtest.ui.interfaces.OnDeviceClickListener

class DeviceAdapter(private val onDeviceClickListener: OnDeviceClickListener) :
    RecyclerView.Adapter<DeviceAdapter.ViewHolder>() {

    private lateinit var binding: BluetoothDeviceCardBinding
    private val deviceList = mutableListOf<BluetoothDevice>()

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {
        fun setData(bluetoothDevice: BluetoothDevice) {
            binding.apply {
                deviceNameText.text = bluetoothDevice.deviceName
                deviceCard.setOnClickListener {
                    onDeviceClickListener.onClick(bluetoothDevice)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.bluetooth_device_card,
            parent,
            false
        )
        return ViewHolder()
    }

    override fun getItemCount(): Int {
        return deviceList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(deviceList[position])
        holder.setIsRecyclable(false)
    }

    fun updateDeviceList(devices: List<BluetoothDevice>) {
        deviceList.apply {
            clear()
            addAll(devices)
        }
        notifyDataSetChanged()
    }
}