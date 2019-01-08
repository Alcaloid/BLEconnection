package com.example.pink.bleconnection.BLEScanner

import android.Manifest
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import com.example.pink.bleconnection.R
import android.bluetooth.BluetoothGatt
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.widget.Toast
import android.os.Build
import android.content.Intent
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat


class BLEscannerNewVersionActivity : AppCompatActivity(){
    lateinit var mBluetoothAdapter: BluetoothAdapter
    private var mBluetoothManager : BluetoothManager? = null
    private val REQUEST_ENABLE_BT = 1
    private var mHandler: Handler? = null
    private val SCAN_PERIOD: Long = 10000
    private var mLEScanner: BluetoothLeScanner? = null
    private var settings: ScanSettings? = null
    private var filters: List<ScanFilter>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_recive_rssi)
        mHandler = Handler()
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            mBluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            mBluetoothAdapter = mBluetoothManager!!.adapter
        }else{
            toast("This device doesn't support BLE")
        }

    }

    override fun onResume() {
        super.onResume()
            val locationPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (!mBluetoothAdapter.isEnabled){
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
        if (locationPermissionCheck!=0){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),REQUEST_ENABLE_BT)
        }
    }
    fun toast(text : String){
        Toast.makeText(this,text, Toast.LENGTH_SHORT).show()
    }
}