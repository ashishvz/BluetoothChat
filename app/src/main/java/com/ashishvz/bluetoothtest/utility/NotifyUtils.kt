package com.ashishvz.bluetoothtest.utility

import android.content.Context
import android.widget.Toast

class NotifyUtils {
    companion object {
        fun showToast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
        fun notifyBluetoothNotEnabled(context: Context) {
            Toast.makeText(context, "Bluetooth Not Enabled!", Toast.LENGTH_SHORT).show()
        }
        fun notifyBluetoothNotEnabledOrPermissionDenied(context: Context) {
            Toast.makeText(context, "Bluetooth Not Enabled OR Permission Denied", Toast.LENGTH_SHORT).show()
        }
        fun notifyPairedDeviceFound(context: Context, numberOfDevices: Int) {
            Toast.makeText(context, "Found $numberOfDevices paired devices", Toast.LENGTH_SHORT).show()
        }
        fun notifyServerDisconnected(context: Context) {
            Toast.makeText(context, "Server Disconnected!", Toast.LENGTH_SHORT).show()
        }
    }
}