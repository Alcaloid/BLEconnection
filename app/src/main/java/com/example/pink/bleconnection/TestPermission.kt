package com.example.pink.bleconnection

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import com.karumi.dexter.Dexter
import android.Manifest
import android.bluetooth.le.BluetoothLeScanner
import android.widget.Toast
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.listener.PermissionRequest


class TestPermission : AppCompatActivity(){

    lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var bluetoothManager: BluetoothManager
    lateinit var bluetoothScanner: BluetoothLeScanner

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        val dialogMultiplePermissionsReport = DialogOnAnyDeniedMultiplePermissionsListener
                .Builder
                .withContext(this)
                .withTitle("Bluetooth & Location")
                .withMessage("Navigator system need bluetooth and location")
                .build()
        Dexter.withActivity(this)
                .withPermissions(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            // do you work now
                            toast("It's work")
                            bluetoothScanner = bluetoothAdapter.bluetoothLeScanner
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                            toast("Navigator System need both of permission")
                        }
                    }
                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken) {
                        toast("Get permission")
                        token.cancelPermissionRequest()
                    }
                }).check()
    }
    fun toast(text : String){
        Toast.makeText(this,text, Toast.LENGTH_SHORT).show()
    }
}