package com.example.pink.bleconnection.Map

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
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


class Maps2Activity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    lateinit var mBluetoothManager : BluetoothManager
    lateinit var mBluetoothAdapter : BluetoothAdapter
    lateinit var mScanner : BluetoothLeScanner

    //0,1 is x,y and 2 is txpower and 3 is rssi and 4 is receive rssi and 5 is distance
    private var beaconInformation : Array<IntArray> = arrayOf(
            intArrayOf(0,0,-69,0,0,0), // beacon0
            intArrayOf(11,0,-74,0,0,0), // beacon1
            intArrayOf(0,15,-77,0,0,0), // beacon2
            intArrayOf(11,15,-77,0,0,0)  // beacon3
    )
    var count : Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps2)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mBluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = mBluetoothManager.adapter
        mScanner = mBluetoothAdapter.bluetoothLeScanner
        mapFragment.getMapAsync(this)
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
        when{
            mMap.cameraPosition.zoom > 15f -> mMap.animateCamera(CameraUpdateFactory.zoomTo(15f))
            mMap.cameraPosition.zoom < 3f -> mMap.animateCamera(CameraUpdateFactory.zoomTo(3f))
        }
        startScanner()
//        mMap.setMinZoomPreference(10.0f) //zoomout
//        mMap.setMaxZoomPreference(14.0f) // zoomin
    }

    private var leScanCallBack = object : ScanCallback(){
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            if(result.device.name == "RL0"){
                beaconInformation[0][3] = result.rssi
                beaconInformation[0][4] = 1
            }else if (result.device.name == "RL1"){
                beaconInformation[1][3] = result.rssi
                beaconInformation[1][4] = 1
            }else if (result.device.name == "RL2"){
                beaconInformation[2][3] = result.rssi
                beaconInformation[2][4] = 1
            }else if (result.device.name == "RL3"){
                beaconInformation[3][3] = result.rssi
                beaconInformation[3][4] = 1
            }
            if (beaconInformation[0][4]+beaconInformation[1][4]+beaconInformation[2][4]+beaconInformation[3][4]>=3){
                for (item in beaconInformation){
                    println(item)
                }
            }
        }
    }
    private fun getDistance(rssi: Int,txPower:Int):Double{
        //d = 10 ^ ((TxPower - RSSI) / (10 * n))
        val n: Int = 2
        val distance : Double = Math.pow(10.0,((txPower-rssi)/(10.0*n)))
        return distance
    }
    fun calLocation(distance1:Int, distance2:Int, distance3:Int){
        val x1: Double = 1.0
        val x2: Double = 1.0
        val x3: Double = 1.0
        val y3: Double = 1.0
        val X : Double = ((distance1*distance1)-(distance2*distance2)+(x2*x2))/(2*x2)
        val Y : Double = ((distance1*distance1)-(distance3*distance3)+(x3*x3)+(y3*y3)-(2*x3*x1))/(2*y3)
        val myLocation : LatLng = LatLng(X,Y)
        mMap.addMarker(MarkerOptions().position(myLocation).title("Marker"))
    }
    override fun onDestroy() {
        super.onDestroy()
        mScanner.stopScan(leScanCallBack)
        beaconInformation[0][4] = 0
        beaconInformation[1][4] = 0
        beaconInformation[2][4] = 0
        beaconInformation[3][4] = 0
    }
    fun startScanner(){
        mScanner.startScan(leScanCallBack)
    }
}
