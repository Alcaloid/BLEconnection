package com.example.pink.bleconnection.BLEScanner

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.pink.bleconnection.R
import kotlinx.android.synthetic.main.activity_bluetooth_recive_rssi.*
import org.jetbrains.annotations.TestOnly
import kotlin.collections.ArrayList



class BluetoothReciveRSSIActivity : AppCompatActivity() {

    lateinit var mBluetoothManager : BluetoothManager
    lateinit var mBluetoothAdapter : BluetoothAdapter
    lateinit var mScanner : BluetoothLeScanner
    lateinit var mHandler : Handler
    lateinit var scanSetting : ScanSettings
    private var rl0RSSI = ArrayList<String>()
    private var rl1RSSI = ArrayList<String>()
    private var rl2RSSI = ArrayList<String>()
    private var rl3RSSI = ArrayList<String>()
    var locationPermissionCheck : Int = 0
    private var uidFilter : ArrayList<ScanFilter> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_recive_rssi)

        val Request_Bluetooth : Int = 1
        var onStartScan : Boolean = false
        mHandler = Handler()
        mBluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = mBluetoothManager.adapter
        locationPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            // BLE is supported, we can use BLE API
            toast("Device support BLE")
            if (!mBluetoothAdapter.isEnabled){
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, Request_Bluetooth)
            }
            if (locationPermissionCheck!=0){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),Request_Bluetooth)
            }
        }
        else {
            // BLE is not supported, Don’t use BLE capabilities here
            val dialog = AlertDialog.Builder(this).setTitle("Alert")
                    .setMessage("This not support Bluetooth")
                    .setPositiveButton("OK"
                    ) { _, _ ->
                        toast("Device don't support BLE")
                        finish()
                    }
            dialog.show()
        }

        mScanner = mBluetoothAdapter.bluetoothLeScanner
            addUUID()
            scanSetting = ScanSettings.Builder().setScanMode(ScanSettings.CALLBACK_TYPE_ALL_MATCHES).build()
        scanner_button.setOnClickListener {
            scanner_button.text = "Stop Scan"
            startScanner()
        }
        scan_2_button.setOnClickListener {
            scan_2_button.text = "Stop Scan"
            startScanner2()
        }
        rl0_button.setOnClickListener {
            showListView("0")
        }
        rl1_button.setOnClickListener {
            showListView("1")
        }
        rl2_button.setOnClickListener {
            showListView("2")
        }
        rl3_button.setOnClickListener {
            showListView("3")
        }
//        reset_button.visibility = View.GONE
        reset_button.setOnClickListener {
            reset()
        }

    }

    private var leScanCallBack = object : ScanCallback(){
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
//            println("Device->"+result.device.name+" Address->"+result.device.address)
            if(result.device.name == "RL0"){
                rl0RSSI.add(result.rssi.toString())
            }else if (result.device.name == "RL1"){
                rl1RSSI.add(result.rssi.toString())
            }else if (result.device.name == "RL2"){
                rl2RSSI.add(result.rssi.toString())
            }else if (result.device.name == "RL3"){
                rl3RSSI.add(result.rssi.toString())
            }
            showListView("0")
        }
    }
    fun showListView(case : String){
        val listview : ArrayList<String>
        if(case.equals("0")){
            listview = rl0RSSI
        }else if(case.equals("1")){
            listview = rl1RSSI
        }else if(case.equals("2")){
            listview = rl2RSSI
        }else if(case.equals("3")){
            listview = rl3RSSI
        }else{
            listview = rl0RSSI //defaual
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listview)
        rssilistview.adapter = adapter
    }
    fun reset() {
        rl0RSSI.clear()
        rl1RSSI.clear()
        rl2RSSI.clear()
        rl3RSSI.clear()
    }
    fun startScanner(){
        toast("start Scanning")
        mHandler.postDelayed({
            stopScanner()
            println("RL0->"+rl0RSSI)
            println("RL1->"+rl1RSSI)
            println("RL2->"+rl2RSSI)
            println("RL3->"+rl3RSSI)
        },10000)
        mScanner.startScan(uidFilter,scanSetting,leScanCallBack)
        mScanner.startScan(leScanCallBack)
    }
    fun startScanner2(){
        toast("start Scanning")
        mHandler.postDelayed({
            stopScanner()
            println("RL0->"+rl0RSSI)
            println("RL1->"+rl1RSSI)
            println("RL2->"+rl2RSSI)
            println("RL3->"+rl3RSSI)
        },10000)
        mScanner.startScan(uidFilter,scanSetting,leScanCallBack)
    }

    fun stopScanner(){
        toast("stop Scanning")
        scanner_button.text = "Scan"
        scan_2_button.text = "Scan"
        mScanner.stopScan(leScanCallBack)
    }
    fun addUUID(){
        uidFilter.add(ScanFilter.Builder().setDeviceAddress("DC:0B:D4:DF:34:7E").build())
        uidFilter.add(ScanFilter.Builder().setDeviceAddress("D3:D8:8B:93:D5:D1").build())
        uidFilter.add(ScanFilter.Builder().setDeviceAddress("E9:56:E4:39:C9:47").build())
        uidFilter.add(ScanFilter.Builder().setDeviceAddress("CD:03:D7:B1:12:96").build())
    }
    override fun onDestroy() {
        super.onDestroy()
        mScanner.stopScan(leScanCallBack)
    }
    fun toast(text : String){
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show()
    }
}

