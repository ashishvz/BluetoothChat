package com.ashishvz.bluetoothtest.ui

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.ashishvz.bluetoothtest.databinding.ActivityMainBinding
import com.ashishvz.bluetoothtest.utility.NotifyUtils

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    /** Enable bluetooth intent launcher */
    private val bluetoothEnabledLauncher: ActivityResultLauncher<Intent>
            = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if (activityResult.resultCode != Activity.RESULT_OK)
            NotifyUtils.notifyBluetoothNotEnabled(applicationContext)
    }

    /** Checking for permissions BLUETOOTH_CONNECT and BLUETOOTH_SCAN */
    private val requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionMap->
        permissionMap.all {
            it.value
        } .let {
            if (it)
                bluetoothEnabledLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            else
                NotifyUtils.showToast(applicationContext, "Permission Denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN))
        else
            bluetoothEnabledLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
    }
}