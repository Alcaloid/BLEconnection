package com.example.pink.bleconnection

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.crazylayout.*
import no.nordicsemi.android.support.v18.scanner.*

class TestBLELibrary_1 : AppCompatActivity(){
    lateinit var scanner : BluetoothLeScannerCompat
    var count : Int =0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.crazylayout)
        scanner = BluetoothLeScannerCompat.getScanner()
        val settings = ScanSettings.Builder()
                .setLegacy(false)
                .setScanMode(ScanSettings.CALLBACK_TYPE_FIRST_MATCH)
                .setUseHardwareBatchingIfSupported(false).build()
        val filters : ArrayList<ScanFilter> = arrayListOf()
        filters.add(ScanFilter.Builder().setDeviceAddress("DC:0B:D4:DF:34:7E").build())
        filters.add(ScanFilter.Builder().setDeviceAddress("D3:D8:8B:93:D5:D1").build())
        filters.add(ScanFilter.Builder().setDeviceAddress("E9:56:E4:39:C9:47").build())
        filters.add(ScanFilter.Builder().setDeviceAddress("CD:03:D7:B1:12:96").build())
        start_button.setOnClickListener {
            Toast.makeText(this,"Start",Toast.LENGTH_SHORT).show()
//            scanner.startScan(scanCallback)
            scanner.startScan(filters, settings, scanCallback)
        }
        stop_button.setOnClickListener {
            scanner.stopScan(scanCallback)
        }
    }

    private var scanCallback = object : ScanCallback(){
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            println("GetData"+count+"->"+result.device.name+" Address:"+result.device.address)
            count += 1
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scanner.stopScan(scanCallback)
    }
}