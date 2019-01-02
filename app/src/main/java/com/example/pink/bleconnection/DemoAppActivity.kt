package com.example.pink.bleconnection

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_demo_app.*

class DemoAppActivity : AppCompatActivity() {

    lateinit var mBluetoothManager : BluetoothManager
    lateinit var mBluetoothAdapter : BluetoothAdapter
    lateinit var mScanner : BluetoothLeScanner
    private var r0Information = ArrayList<String>()
    private var rlInformation = ArrayList<String>()
    private var r2Information = ArrayList<String>()
    private var r3Information = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo_app)

        var onStartScan : Boolean = false
        var testRSSI1 : Int = 0
        var testRSSI2 : Int = 0
        var testRSSI3 : Int = 0

        setBeaconInformation()
        mBluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = mBluetoothManager.adapter
        mScanner = mBluetoothAdapter.bluetoothLeScanner

        calulate_button.setOnClickListener {
            testRSSI1 = Integer.parseInt(editText_Beacon1.text.toString())
            testRSSI2 = Integer.parseInt(editText_Beacon2.text.toString())
            testRSSI3 = Integer.parseInt(editText_Beacon3.text.toString())

        }
        scan_button.setOnClickListener {
            if (onStartScan){
                onStartScan = !onStartScan
                scan_button.text = "Scan"
                stopScanner()
            }else{
                onStartScan = !onStartScan
                scan_button.text = "Stop Scan"
                startScanner()
            }
        }
    }

    private var leScanCallBack = object : ScanCallback(){
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)

        }
    }
    fun calculateScanLocation(beacon1:String,beacon2: String,beacon3: String){

    }
    fun calculateTestLocation(rssi1:Int,rssi2: Int,rssi3: Int){
        val x1 : Int
        val x2 : Int
        val x3 : Int
        val y1 : Int
        val y2 : Int
        val y3 : Int
        
    }
    fun setBeaconInformation(){
        //0,1 is x,y
        //2 is rssi

    }

    fun startScanner(){
        toast("start Scanning")
        mScanner.startScan(leScanCallBack)
    }
    fun stopScanner(){
        toast("stop Scanning")
        mScanner.stopScan(leScanCallBack)
    }
    fun toast(text : String){
        Toast.makeText(this,text, Toast.LENGTH_SHORT).show()
    }
    override fun onDestroy() {
        super.onDestroy()
        mScanner.stopScan(leScanCallBack)
    }
}
