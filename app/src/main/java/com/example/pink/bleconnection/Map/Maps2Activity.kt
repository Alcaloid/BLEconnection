package com.example.pink.bleconnection.Map

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
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
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_maps2.*

class Maps2Activity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    lateinit var mBluetoothManager : BluetoothManager
    lateinit var mBluetoothAdapter : BluetoothAdapter
    lateinit var mScanner : BluetoothLeScanner
    lateinit var isMyLocation : Marker
    lateinit var mHandler: Handler
    lateinit var scanSetting : ScanSettings

    private var beaconInformation : Array<IntArray> = arrayOf(
            //0,1 is x,y and 2 is txpower
            intArrayOf(0,0,-64), // beacon0
            intArrayOf(15,0,-68), // beacon1
            intArrayOf(0,11,-68), // beacon2
            intArrayOf(15,11,-68)  // beacon3
    )
    private var dataDistance : Array<DoubleArray> = arrayOf(
            //0 is uuid and 1 is distance
            doubleArrayOf(0.0,1000.0),
            doubleArrayOf(1.0,1000.0),
            doubleArrayOf(2.0,1000.0),
            doubleArrayOf(3.0,1000.0)
    )
    private var beaconSignal : Array<ArrayList<Int>> = arrayOf(
            arrayListOf(),
            arrayListOf(),
            arrayListOf(),
            arrayListOf()
    )
    private var placeName : ArrayList<String> = arrayListOf(
            "ZoneA","ZoneB","ZoneC","ZoneD"
    )
    private var uidFilter : ArrayList<ScanFilter> = arrayListOf()
    var searchMarker : Marker? = null
    var canNavigator : Boolean = false
    var tmp : DoubleArray = doubleArrayOf()
    var count : Int = 0
    var focusMap : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps2)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mHandler = Handler()
        var buff : String = ""
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

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, placeName)
        mListPlaceName.setAdapter(adapter)

        button_map_start_search.setOnClickListener {
            searchBackground.setBackgroundColor(resources.getColor(R.color.angel_white))
            search_font.visibility = View.GONE
            search_backend.visibility = View.VISIBLE
            mListPlaceName.visibility = View.VISIBLE
        }
        button_map_cloesr.setOnClickListener {
            searchBackground.setBackgroundColor(Color.TRANSPARENT)
            buff = editText_search_place.text.toString()
            if (!buff.equals("")){
                checkSearch(buff)
            }
            mListPlaceName.visibility = View.GONE
            search_backend.visibility = View.GONE
            search_font.visibility = View.VISIBLE
        }
        editText_search_place.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int){}
            override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter.filter.filter(str)
            }
        })
        mListPlaceName.setOnItemClickListener { parent, view, position, id ->
            editText_search_place.setText(placeName[position])
        }
        searchBackground.setOnClickListener {
            if (focusMap){
                search_background.visibility = View.VISIBLE
            }else{
                search_background.visibility = View.GONE
            }
            focusMap = !focusMap
        }
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
        val testingRoom = LatLngBounds(
                LatLng(0.0, 0.0),
                LatLng(15.0,11.0))
        val mapGroundOverLay = GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.seniortesting))
                .positionFromBounds(testingRoom).zIndex(1f)
        val locationZoom = LatLng(7.5,5.5)
        val cameraTraget = LatLngBounds(
                LatLng(-0.01,-0.01), LatLng(16.0, 12.0))

        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NONE
        mMap.addGroundOverlay(mapGroundOverLay)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationZoom,5f))
        mMap.setLatLngBoundsForCameraTarget(cameraTraget)
        isMyLocation = mMap.addMarker(MarkerOptions().position(LatLng(-1000.0,-1000.0)).title("Mark"))
        mMap.uiSettings.isRotateGesturesEnabled = false
        mMap.uiSettings.isMapToolbarEnabled = false
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
            scanSetting = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
            startScanner()
        }
    }
    private var leScanCallBack = object : ScanCallback(){
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
//            println("GetData->"+result.device.name+" Address:"+result.device.address)
            if(result.device.name == "RL0"){
                beaconSignal[0].add(result.rssi)
            }else if (result.device.name == "RL1"){
                beaconSignal[1].add(result.rssi)
            }else if (result.device.name == "RL2"){
                beaconSignal[2].add(result.rssi)
            }else if (result.device.name == "RL3"){
                beaconSignal[3].add(result.rssi)
            }
        }
    }
    private fun getDistance(rssi: Int,txPower:Int):Double{
        //d = 10 ^ ((TxPower - RSSI) / (10 * n))
        val n: Int = 2
        val distance : Double = Math.pow(10.0,((txPower-rssi)/(10.0*n)))
//        println("Distance:"+distance)
        return distance
    }
    private fun markLocation(beacon1:Int, beacon2:Int, beacon3:Int,distance1:Double,distance2:Double,distance3:Double){
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
        val finalX = (((valueC*valueE) - (valueF*valueB))/((valueE*valueA)-(valueB*valueD)))
        val finalY = (((valueC*valueD)-(valueA*valueF))/((valueB*valueD)-(valueA*valueE)))
        val myLocation : LatLng = LatLng(finalX,finalY)

        isMyLocation.remove()
        isMyLocation = mMap.addMarker(MarkerOptions().position(myLocation).title(count.toString()))
        count += 1
        setStartData()
    }
    fun setStartData(){
        for (i in 0..beaconSignal.size-1){
            beaconSignal[i].clear()
            dataDistance[i][1] = 1000.0
        }
        startScanner()
    }
    fun findMyLocation(){
        val averageSignal : Array<Int> = arrayOf(0,0,0,0)
        for (i in 0..beaconSignal.size-1){
            averageSignal[i] = findAverage(beaconSignal[i])
            if (averageSignal[i]!= -100000) {
//                println("Beacon is "+dataDistance[i][0])
                dataDistance[i][1] = getDistance(averageSignal[i],beaconInformation[i][2])
            }
        }
        distanceMinToMax()
        if ((dataDistance[0][1] != 1000.0)&&(dataDistance[1][1]!=1000.0)&&(dataDistance[2][1]!=1000.0)){
            markLocation(dataDistance[0][0].toInt(),dataDistance[1][0].toInt(),dataDistance[2][0].toInt(),
                    dataDistance[0][1],dataDistance[1][1],dataDistance[2][1])
        }else{
            startScanner() //get more signal
        }
    }
    fun findAverage(dataArray: ArrayList<Int>):Int{
        var averageNumber : Int = -100000
        if (!dataArray.isEmpty()){
            averageNumber = 0
            for (i in 0..dataArray.size-1){
                averageNumber = averageNumber + dataArray[i]
            }
            averageNumber = averageNumber/dataArray.size
        }
        return averageNumber
    }
    fun distanceMinToMax(){
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
    }
    fun startScanner(){
        mHandler.postDelayed({
            stopScanner()
            findMyLocation()
        },5000)
        mScanner.startScan(null,scanSetting,leScanCallBack)
    }
    fun stopScanner(){
        mScanner.stopScan(leScanCallBack)
    }
    fun checkSearch(string: String){
        val buff = string.toLowerCase()
        when(buff){
            "zonea" -> markSearchRoom(LatLng(3.75,2.75),"ZoneA")
            "zoneb" -> markSearchRoom(LatLng(11.25,2.75),"ZoneB")
            "zonec" -> markSearchRoom(LatLng(3.75,8.25),"ZoneC")
            "zoned" -> markSearchRoom(LatLng(11.25,8.25),"ZoneD")
            else -> {
                if (searchMarker != null) searchMarker?.remove()
                toast("Doesn't found room")
            }
        }
    }
    fun markSearchRoom(roomPosition : LatLng,roomName : String){
        if (searchMarker == null){
            searchMarker = mMap.addMarker(MarkerOptions().position(roomPosition).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title(roomName))
        }else{
            searchMarker?.remove()
            searchMarker = mMap.addMarker(MarkerOptions().position(roomPosition).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title(roomName))
        }
    }
    fun toast(text : String){
        Toast.makeText(this,text, Toast.LENGTH_SHORT).show()
    }
    fun colseSoftKeyboard(){
        val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.SHOW_FORCED)
    }
    override fun onDestroy() {
        super.onDestroy()
        mScanner.stopScan(leScanCallBack)
    }


}
