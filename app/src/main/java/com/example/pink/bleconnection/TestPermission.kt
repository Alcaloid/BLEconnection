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
import android.R.string.cancel
import android.content.DialogInterface
import android.content.Intent
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog


class TestPermission : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        toast("Start")
        val dialogMultiplePermissionsReport = DialogOnAnyDeniedMultiplePermissionsListener
                .Builder
                .withContext(this)
                .withTitle("Bluetooth & Location")
                .withMessage("Navigator system need bluetooth and location")
                .withButtonText("Ok")
                .build()
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION
                        //BluetoothAdapter.ACTION_REQUEST_ENABLE,
                        //Manifest.permission.ACCESS_COARSE_LOCATION
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            // do you work now
                            toast("It's work")
                            //bluetoothScanner = bluetoothAdapter.bluetoothLeScanner
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                            toast("Navigator System need both of permission")
                            //showSettingsDialog()
                        }
                    }
                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken) {
                        toast("Get permission")
                        token.continuePermissionRequest()
                    }
                }).check()
    }

    private fun showSettingsDialog() {
        val dialog = AlertDialog.Builder(this).setTitle("Alert")
                .setMessage("Need Permission")
                .setPositiveButton("OK"
                ) { _, _ ->
                    startPermission()
                }
        dialog.show()
    }
    fun startPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),1)
    }
    fun toast(text : String){
        Toast.makeText(this,text, Toast.LENGTH_SHORT).show()
    }
}