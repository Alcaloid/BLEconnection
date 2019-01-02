package com.example.pink.bleconnection

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import android.content.Intent
import android.content.IntentFilter
import kotlinx.android.synthetic.main.activity_scanbluetooth.*
import android.annotation.SuppressLint
import android.view.View
import android.R.attr.action
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.widget.ArrayAdapter
import android.widget.LinearLayout


class ScanBluetoothDevice : AppCompatActivity(){

    private var deviceBluetooth : ArrayList<String> = arrayListOf()
    private var deviceRSSI : ArrayList<String> = arrayListOf()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanbluetooth)
        val BTAdapter = BluetoothAdapter.getDefaultAdapter()
        val Request_Bluetooth : Int = 1
        val locationPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        val dialog : AlertDialog.Builder

        //Bluetooth
        if (BTAdapter == null) {
            testing_text.setText("Device has not support bluetooth")
            dialog = AlertDialog.Builder(this).setTitle("Alert")
                    .setMessage("This not support Bluetooth")
                    .setPositiveButton("OK"
                    ) { _, _ ->
                        toast("This not support bluetooth")
                        finish()
                    }
            dialog.show()
        } else {
            testing_text.setText("Click button to start discovery BT device")
            if (!BTAdapter.isEnabled) {
                val enableBT = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBT, Request_Bluetooth)
            }
            if (locationPermissionCheck!=0){
                //permission location
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),Request_Bluetooth)
            }
        }

        if(BTAdapter.startDiscovery()){
            BTAdapter.cancelDiscovery()
        }
        BTAdapter.startDiscovery()

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mReceiver,filter)

        discover_button.setOnClickListener {
            toast("On progress")
            deviceBluetooth.clear()
            deviceRSSI.clear()
            if (BTAdapter.startDiscovery()){
                BTAdapter.cancelDiscovery()
            }
            BTAdapter.startDiscovery()
        }

        bluetoothdevicelist.setOnItemClickListener { parent, view, position, id ->
            toast("RSSI is "+deviceRSSI[position])
        }

    }

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action : String = intent.action
            when(action){
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device.name
                    val rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE)
                    val deviceHardwareAddress = device.address // MAC address
                    println("Device is " + deviceName + " RSSI is " + rssi)
                    Toast.makeText(this@ScanBluetoothDevice, "Device is " + deviceName + " and rssi is " + rssi, Toast.LENGTH_SHORT).show()
                    if (deviceName == null){
                        deviceBluetooth.add("null")
                    }else{
                        deviceBluetooth.add(deviceName)
                    }
                    deviceRSSI.add(rssi.toString())
                    listBluetoothDevice()
                }
            }
        }
    }

    fun listBluetoothDevice(){
        if (deviceBluetooth.isEmpty()){
            bluetoothtextscanner.visibility = View.VISIBLE
        }else{
            bluetoothtextscanner.visibility = View.GONE
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceBluetooth)
            bluetoothdevicelist.adapter = adapter

//            deviceinformationlist.layoutManager = LinearLayoutManager(this)
//            deviceinformationlist.adapter = ShowBluetoothScannerDeviceAdapter(deviceBluetooth,deviceRSSI,this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
    }
    fun toast(text : String){
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show()
    }
}