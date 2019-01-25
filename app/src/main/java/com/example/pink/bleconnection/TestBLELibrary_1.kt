package com.example.pink.bleconnection

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.crazylayout.*
import no.nordicsemi.android.support.v18.scanner.*

class TestBLELibrary_1 : AppCompatActivity(){
    lateinit var scanner : BluetoothLeScannerCompat
    private var rl0RSSI = ArrayList<String>()
    private var rl1RSSI = ArrayList<String>()
    private var rl2RSSI = ArrayList<String>()
    private var rl3RSSI = ArrayList<String>()
    var count : Int =0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.crazylayout)
        scanner = BluetoothLeScannerCompat.getScanner()
        val settings = ScanSettings.Builder()
                .setLegacy(false)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setUseHardwareBatchingIfSupported(false).build()
//        val filters : ArrayList<ScanFilter> = arrayListOf()
//        filters.add(ScanFilter.Builder().setDeviceAddress("DC:0B:D4:DF:34:7E").build())
//        filters.add(ScanFilter.Builder().setDeviceAddress("D3:D8:8B:93:D5:D1").build())
//        filters.add(ScanFilter.Builder().setDeviceAddress("E9:56:E4:39:C9:47").build())
//        filters.add(ScanFilter.Builder().setDeviceAddress("CD:03:D7:B1:12:96").build())
        start_button.setOnClickListener {
            Toast.makeText(this,"Start",Toast.LENGTH_SHORT).show()
//            scanner.startScan(scanCallback)
            scanner.startScan(null, settings, scanCallback)
        }
        stop_button.setOnClickListener {
            scanner.stopScan(scanCallback)
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
        reset_button.setOnClickListener {
            reset()
        }
        showListView("0")
    }

    private var scanCallback = object : ScanCallback(){
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            println("GetData"+count+"->"+result.device.name+" Address:"+result.device.address)
//            count += 1
            if(result.device.name == "RL0"){
                rl0RSSI.add(result.rssi.toString())
            }else if (result.device.name == "RL1"){
                rl1RSSI.add(result.rssi.toString())
            }else if (result.device.name == "RL2"){
                rl2RSSI.add(result.rssi.toString())
            }else if (result.device.name == "RL3"){
                rl3RSSI.add(result.rssi.toString())
            }
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
        resultlist.adapter = adapter
    }
    fun reset() {
        rl0RSSI.clear()
        rl1RSSI.clear()
        rl2RSSI.clear()
        rl3RSSI.clear()
        count = 0
    }
    override fun onDestroy() {
        super.onDestroy()
        scanner.stopScan(scanCallback)
    }
}