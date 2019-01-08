package com.example.pink.bleconnection.Map

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.example.pink.bleconnection.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import android.support.v4.app.ActivityCompat





class Maps2Activity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    lateinit var mBluetoothManager : BluetoothManager
    lateinit var mBluetoothAdapter : BluetoothAdapter
    lateinit var mScanner : BluetoothLeScanner
    lateinit var isMyLocation : Marker
    lateinit var mHandler: Handler

    //0,1 is x,y and 2 is txpower and 3 is distance from rssi and 4 is receive rssi and 5 is distance
    private var beaconInformation : Array<IntArray> = arrayOf(
            intArrayOf(0,0,-69,0), // beacon0
            intArrayOf(11,0,-74,0), // beacon1
            intArrayOf(0,15,-77,0), // beacon2
            intArrayOf(11,15,-77,0)  // beacon3
    )
    private var dataDistance : Array<DoubleArray> = arrayOf(
            //0 is uuid and 1 is distance
            doubleArrayOf(0.0,1000.0),
            doubleArrayOf(1.0,1000.0),
            doubleArrayOf(2.0,1000.0),
            doubleArrayOf(3.0,1000.0)
    )
    var canNavigator : Boolean = false
    var tmp : DoubleArray = doubleArrayOf()
    var lastLocationloc: Location? = null
    var startLocation : Location? = null
    var myLocation : Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps2)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mHandler = Handler()
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            canNavigator = true
        }else{
            canNavigator = false
            toast("This Device can't support BLE")
        }
        if (canNavigator){
            mBluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            mBluetoothAdapter = mBluetoothManager.adapter
        }
        mapFragment.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        val locationPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        val REQUEST_ENABLE_BT :Int = 1
        if(canNavigator){
            if (!mBluetoothAdapter.isEnabled){
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
            if (locationPermissionCheck!=0){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),REQUEST_ENABLE_BT)
            }
        }
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
        val locationZoom = LatLng(5.5,7.5)
        val cameraTraget = LatLngBounds(
                LatLng(-0.01,-0.01), LatLng(20.0, 20.0))
        mMap.addGroundOverlay(testingMap)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationZoom,5f))
        mMap.setLatLngBoundsForCameraTarget(cameraTraget)
        isMyLocation = mMap.addMarker(MarkerOptions().position(LatLng(-1000.0,-1000.0)).title("Mark"))
        mMap.uiSettings.isRotateGesturesEnabled = false
        mMap.setOnCameraChangeListener {
            val maxZoom = 8.0f;
            val minZoom = 5.0f
            if (mMap.cameraPosition.zoom > maxZoom){
                mMap.animateCamera(CameraUpdateFactory.zoomTo(maxZoom))
            }else if(mMap.cameraPosition.zoom < minZoom){
                mMap.animateCamera(CameraUpdateFactory.zoomTo(minZoom))
            }
        }
        if (canNavigator){
            mScanner = mBluetoothAdapter.bluetoothLeScanner
            startScanner()
        }
    }

    private var leScanCallBack = object : ScanCallback(){
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            if(result.device.name == "RL0"){
                dataDistance[0][1] = getDistance(result.rssi,beaconInformation[0][2])
                beaconInformation[0][3] = 1
            }else if (result.device.name == "RL1"){
                dataDistance[1][1] = getDistance(result.rssi,beaconInformation[1][2])
                beaconInformation[1][3] = 1
            }else if (result.device.name == "RL2"){
                dataDistance[2][1] = getDistance(result.rssi,beaconInformation[2][2])
                beaconInformation[2][3] = 1
            }else if (result.device.name == "RL3"){
                dataDistance[3][1] = getDistance(result.rssi,beaconInformation[3][2])
                beaconInformation[3][3] = 1
            }
            if (beaconInformation[0][3]+beaconInformation[1][3]+beaconInformation[2][3]+beaconInformation[3][3]>=3){
                for (i in 0..dataDistance.size-1){
                    for (j in 0..dataDistance.size-1){
                        if (j != dataDistance.size-1){
                            if (dataDistance[j][1] > dataDistance[j+1][1]){
                                tmp = dataDistance[j]
                                dataDistance[j] = dataDistance[j+1]
                                dataDistance[j+1] = tmp
                            }
                        }
                    }
                }
                markLocation(dataDistance[0][0].toInt(),dataDistance[1][0].toInt(),dataDistance[2][0].toInt(),
                        dataDistance[0][1],dataDistance[1][1],dataDistance[2][1])

            }
        }
    }

    private fun getDistance(rssi: Int,txPower:Int):Double{
        //d = 10 ^ ((TxPower - RSSI) / (10 * n))
        val n: Int = 2
        val distance : Double = Math.pow(10.0,((txPower-rssi)/(10.0*n)))
        return distance
    }
    fun markLocation(beacon1:Int, beacon2:Int, beacon3:Int,distance1:Double,distance2:Double,distance3:Double){
        val x1 = beaconInformation[beacon1][0]
        val x2 = beaconInformation[beacon2][0]
        val x3 = beaconInformation[beacon3][0]
        val y1 = beaconInformation[beacon1][1]
        val y2 = beaconInformation[beacon2][1]
        val y3 = beaconInformation[beacon3][1]
        val valueA = (-2*x1) + (2*x2)
        val valueB = (-2*y1) + (2*y2)
        val valueC = (distance1*distance1) - (distance2*distance2) - (x1*x1) + (x2*x2) - (y1*y1) + (y2*y2)
        val valueD = (-2*x2) + (2*x3)
        val valueE = (-2*y2) + (2*y3)
        val valueF = (distance2*distance2) - (distance3*distance3) - (x2*x2) + (x3*x3) - (y2*y2) + (y3*y3)
        val finalX = (((valueC*valueE) - (valueF*valueB))/((valueE*valueA)-(valueB*valueD))).toDouble()
        val finalY = (((valueC*valueD)-(valueA*valueF))/((valueB*valueD)-(valueA*valueE))).toDouble()
//        if (startLocation == null){
//            startLocation =
//        }
        val myLocation : LatLng = LatLng(finalX,finalY)
        isMyLocation.remove()
        isMyLocation = mMap.addMarker(MarkerOptions().position(myLocation).title("MyLocation"))
    }
    override fun onDestroy() {
        super.onDestroy()
        mScanner.stopScan(leScanCallBack)
        beaconInformation[0][3] = 0
        beaconInformation[1][3] = 0
        beaconInformation[2][3] = 0
        beaconInformation[3][3] = 0
    }
    fun startScanner(){
        mScanner.startScan(leScanCallBack)
    }
    fun stopScanner(){
        mScanner.stopScan(leScanCallBack)
    }
    fun toast(text : String){
        Toast.makeText(this,text, Toast.LENGTH_SHORT).show()
    }
}
