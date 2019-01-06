package com.example.pink.bleconnection.BLEScanner

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


class BLEscannerNewVersionActivity : AppCompatActivity(){
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothManager : BluetoothManager? = null
    private val REQUEST_ENABLE_BT = 1
    private var mHandler: Handler? = null
    private val SCAN_PERIOD: Long = 10000
    private var mLEScanner: BluetoothLeScanner? = null
    private val settings: ScanSettings? = null
    private val filters: List<ScanFilter>? = null

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
//        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            Toast.makeText(this, "BLE Not Supported",
//                    Toast.LENGTH_SHORT).show();
//            finish();
//        }
//        final BluetoothManager bluetoothManager =
//                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    override fun onResume() {
        super.onResume()

    }
    fun toast(text : String){
        Toast.makeText(this,text, Toast.LENGTH_SHORT).show()
    }
}