package com.example.pink.bleconnection.Map

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.pink.bleconnection.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.CameraPosition


class Maps2Activity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    lateinit var mBluetoothManager : BluetoothManager
    lateinit var mBluetoothAdapter : BluetoothAdapter
    lateinit var mScanner : BluetoothLeScanner
    //0,1 is x,y and 3 is rssi and 4 is receive rssi
    private var beacon0Information : IntArray = intArrayOf(0,0,0,0)
    private var beacon1Information : IntArray = intArrayOf(11,0,0,0)
    private var beacon2Information : IntArray = intArrayOf(0,15,0,0)
    private var beacon3Information : IntArray = intArrayOf(11,15,0,0)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps2)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mBluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = mBluetoothManager.adapter
        mScanner = mBluetoothAdapter.bluetoothLeScanner
//        beacon0Information[0] = 20
//        println("array size : "+ beacon0Information.size + " position x,y : "+beacon0Information[0]+","+beacon0Information[1])
        mapFragment.getMapAsync(this)
//        startScanner()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NONE
        val testingRoom = LatLngBounds(
                LatLng(0.0, 0.0),
                LatLng(11.0,15.0))
        val testingMap = GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.seniortesting))
                .positionFromBounds(testingRoom)
        val location = LatLng(0.0,0.0)
        val locationZoom = LatLng(5.5,7.5)
        val cameraTraget = LatLngBounds(
                LatLng(-0.01,-0.01), LatLng(20.0, 20.0))
        mMap.addGroundOverlay(testingMap)
        mMap.addMarker(MarkerOptions().position(location).title("Marker"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationZoom,10f))
        mMap.setLatLngBoundsForCameraTarget(cameraTraget);

        startScanner()
//        mMap.setMinZoomPreference(10.0f) //zoomout
//        mMap.setMaxZoomPreference(14.0f) // zoomin
    }

    private var leScanCallBack = object : ScanCallback(){
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            if(result.device.name == "RL0"){
                beacon0Information[2] = result.rssi
                beacon0Information[3] = 1
            }else if (result.device.name == "RL1"){
                beacon1Information[2] = result.rssi
                beacon1Information[3] = 1
            }else if (result.device.name == "RL2"){
                beacon2Information[2] = result.rssi
                beacon2Information[3] = 1
            }else if (result.device.name == "RL3"){
                beacon3Information[2] = result.rssi
                beacon3Information[3] = 1
            }
//            if (beacon0Information[3]+beacon1Information[3]+beacon2Information[3]+beacon3Information[3]>=3){
//                calLocation(beacon0Information[2],beacon1Information[2],beacon2Information[2])
//            }
        }
    }
    private fun getDistance(rssi: Int,txPower:Int):Double{
        //d = 10 ^ ((TxPower - RSSI) / (10 * n))
        val n: Int = 2
        val distance : Double = Math.pow(10.0,((txPower-rssi)/(10.0*n)))
        return distance
    }
    fun calLocation(R1:Int,R2:Int,R3:Int){
        val x1: Double = 1.0
        val x2: Double = 1.0
        val x3: Double = 1.0
        val y3: Double = 1.0
        val X : Double = ((R1*R1)-(R2*R2)+(x2*x2))/(2*x2)
        val Y : Double = ((R1*R1)-(R3*R3)+(x3*x3)+(y3*y3)-(2*x3*x1))/(2*y3)
        val myLocation : LatLng = LatLng(X,Y)
        mMap.addMarker(MarkerOptions().position(myLocation).title("Marker"))
    }
    override fun onDestroy() {
        super.onDestroy()
        mScanner.stopScan(leScanCallBack)
        beacon0Information[3] = 0
        beacon1Information[3] = 0
        beacon2Information[3] = 0
        beacon3Information[3] = 0
    }
    fun startScanner(){
        mScanner.startScan(leScanCallBack)
    }
}
