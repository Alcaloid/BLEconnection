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
import com.example.pink.bleconnection.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import com.example.pink.bleconnection.Model.BeaconDetail
import com.example.pink.bleconnection.Model.CalculatorFunction
import com.example.pink.bleconnection.Model.PointOfLine
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_maps2.*

class Maps2Activity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var mBluetoothManager : BluetoothManager? = null
    private var mBluetoothAdapter : BluetoothAdapter? = null
    private var mScanner : BluetoothLeScanner? = null
    private var mHandler: Handler? = null
    lateinit var scanSetting : ScanSettings
    private var beaconDetail : ArrayList<BeaconDetail> = arrayListOf()
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
    private var pointOfLine : ArrayList<PointOfLine> = arrayListOf()
    private var placeName : ArrayList<String> = arrayListOf(
            "ZoneA","ZoneB","ZoneC","ZoneD"
    )
    private var isMyLocation : Marker? = null
    private var myLocation : LatLng? = null
    private var polyLine : Polyline? = null
    private var calFunction : CalculatorFunction = CalculatorFunction()
    private var showMyLocation = false
    var searchMarker : Marker? = null
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
        mBluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = mBluetoothManager?.adapter
        mapFragment.getMapAsync(this)
        button_background.setOnClickListener {
            calFunction.toast("Coming Soon",this@Maps2Activity)
        }
        setBeaconInformation()
        searchOperation()
        navigationOperation()
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
                .positionFromBounds(testingRoom).zIndex(0f)
        val locationZoom = LatLng(7.5,5.5)
        val cameraTraget = LatLngBounds(
                LatLng(-0.01,-0.01), LatLng(16.0, 12.0))

        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NONE
        mMap.addGroundOverlay(mapGroundOverLay)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationZoom,5f))
        mMap.setLatLngBoundsForCameraTarget(cameraTraget)
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
        settingAndCheckPermission()
    }

    private fun settingAndCheckPermission(){
        if(packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            mBluetoothAdapter?.takeIf { !it.isEnabled }?.apply {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, 1)
            }
            Dexter.withActivity(this)
                    .withPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    .withListener(object : PermissionListener {
                        override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                            showMyLocation = true
                            mScanner = mBluetoothAdapter?.bluetoothLeScanner
                            scanSetting = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
                            startScanner()
                        }
                        override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                            calFunction.toast("Navigator system need location permission",this@Maps2Activity)
                        }
                        override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                            token.continuePermissionRequest()
                        }
                    }).check()
        }else{
            calFunction.toast("This Device can't use navigator",this@Maps2Activity)
        }
    }
    private var leScanCallBack = object : ScanCallback(){
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            println("GetData->"+result.device.name+" Address:"+result.device.address)
            if(result.device.name == "RL0"){
                beaconDetail[0].addSignal(result.rssi)
//                beaconSignal[0].add(result.rssi)
            }else if (result.device.name == "RL1"){
                beaconDetail[1].addSignal(result.rssi)
//                beaconSignal[1].add(result.rssi)
            }else if (result.device.name == "RL2"){
                beaconDetail[2].addSignal(result.rssi)
//                beaconSignal[2].add(result.rssi)
            }else if (result.device.name == "RL3"){
                beaconDetail[3].addSignal(result.rssi)
//                beaconSignal[3].add(result.rssi)
            }
        }
    }
    fun findMyLocation(){
        var average : Double = 0.0
        for (i in 0..beaconDetail.size){
            average = beaconDetail[i].getAverageSignal()
            if (average != 0.0){
                //can get signal one or more
                //find distance
                dataDistance[i][1] = calFunction.calDistanceFromRSSI(average,beaconDetail[i].getPower())
            }
        }
        distanceMinToMax()
        if ((dataDistance[0][1] != 1000.0)&&(dataDistance[1][1]!=1000.0)&&(dataDistance[2][1]!=1000.0)){
            markLocation(dataDistance[0][0].toInt(),dataDistance[1][0].toInt(),dataDistance[2][0].toInt(),
                    dataDistance[0][1],dataDistance[1][1],dataDistance[2][1])
        }else{
            startScanner() //get more signal
        }
        /*val averageSignal : Array<Int> = arrayOf(0,0,0,0)
        for (i in 0..beaconSignal.size-1){
            averageSignal[i] = calFunction.calAverage(beaconSignal[i])
            if (averageSignal[i]!= -100000) {
                dataDistance[i][1] = calFunction.calDistanceFromRSSI(averageSignal[i],beaconInformation[i][2])
            }
        }
        distanceMinToMax()
        if ((dataDistance[0][1] != 1000.0)&&(dataDistance[1][1]!=1000.0)&&(dataDistance[2][1]!=1000.0)){
            markLocation(dataDistance[0][0].toInt(),dataDistance[1][0].toInt(),dataDistance[2][0].toInt(),
                    dataDistance[0][1],dataDistance[1][1],dataDistance[2][1])
        }else{
            startScanner() //get more signal
        }*/
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
    fun markLocation(beacon1:Int, beacon2:Int, beacon3:Int,distance1:Double,distance2:Double,distance3:Double){
        val x1 = beaconDetail[beacon1].getPosition().latitude//beaconInformation[beacon1][0]
        val x2 = beaconDetail[beacon2].getPosition().latitude//beaconInformation[beacon2][0]
        val x3 = beaconDetail[beacon3].getPosition().latitude//beaconInformation[beacon3][0]
        val y1 = beaconDetail[beacon1].getPosition().longitude//beaconInformation[beacon1][1]
        val y2 = beaconDetail[beacon2].getPosition().longitude//beaconInformation[beacon2][1]
        val y3 = beaconDetail[beacon3].getPosition().longitude//beaconInformation[beacon3][1]
        myLocation = calFunction.calLocation(x1,x2,x3,y1,y2,y3,distance1,distance2,distance3)
        if (isMyLocation != null){
            isMyLocation?.remove()
        }
        isMyLocation = mMap.addMarker(MarkerOptions().position(myLocation!!).title(count.toString()))
        count += 1
        setStartData()
    }
    fun setStartData(){
        for (i in 0..beaconDetail.size){
            beaconDetail[i].clearSignal()
            dataDistance[i][1] = 1000.0
        }
        /*for (i in 0..beaconSignal.size-1){
            beaconSignal[i].clear()
            dataDistance[i][1] = 1000.0
        }*/
        startScanner()
    }

    fun functionSearch(oprea : String){
        var buff : String = ""
        when(oprea){
            "open" -> {
                searchBackground.setBackgroundColor(resources.getColor(R.color.angel_white))
                search_font_2.visibility = View.GONE
                search_back_2.visibility = View.VISIBLE
                mListPlaceName.visibility = View.VISIBLE
            }
            "search" -> {
                searchBackground.setBackgroundColor(Color.TRANSPARENT)
                mListPlaceName.visibility = View.GONE
                buff = editText_search_place_2.text.toString()
                if (!buff.equals("")){
                    checkSearch(buff)
                }
                search_back_2.visibility = View.GONE
                search_font_2.visibility = View.VISIBLE
            }
            "found"->{
                line_1.visibility = View.GONE
                button_navigation.visibility = View.GONE
                button_search_delete_text.visibility = View.VISIBLE
                text_show_search.text = editText_search_place_2.text.toString()
                text_show_search.setTextColor(Color.BLACK)
            }
            "close" -> {
                searchBackground.setBackgroundColor(Color.TRANSPARENT)
                mListPlaceName.visibility = View.GONE
                search_back_2.visibility = View.GONE
                button_search_delete_text.visibility = View.GONE
                editText_search_place_2.text.clear()
                if (searchMarker != null){
                    searchMarker?.remove()
                }

                text_show_search.text = getString(R.string.searchtext)
                text_show_search.setTextColor(Color.GRAY)

                search_font_2.visibility = View.VISIBLE
                line_1.visibility = View.VISIBLE
                button_navigation.visibility = View.VISIBLE
            }
            else -> {
                calFunction.toast("Set string error",this@Maps2Activity)
                }
        }
    }
    fun searchOperation(){
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, placeName)
        mListPlaceName.setAdapter(adapter)
        text_show_search.setOnClickListener {
            functionSearch("open")
        }
        search_back_to_font.setOnClickListener {
            functionSearch("close")
        }
        editText_search_place_2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int){}
            override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter.filter.filter(str)
            }
        })
        button_search.setOnClickListener {
            functionSearch("search")
        }
        button_search_delete_text.setOnClickListener {
            functionSearch("close")
        }
        mListPlaceName.setOnItemClickListener { parent, view, position, id ->
            editText_search_place_2.setText(adapter.getItem(position))
        }
    }
    fun checkSearch(string: String){
        val buff = string.toLowerCase()
        when(buff){
            "zonea" -> markSearchRoom(LatLng(3.75,2.75),"ZoneA")
            "zoneb" -> markSearchRoom(LatLng(11.25,2.75),"ZoneB")
            "zonec" -> markSearchRoom(LatLng(3.75,8.25),"ZoneC")
            "zoned" -> markSearchRoom(LatLng(11.25,8.25),"ZoneD")
            else -> {
//                if (searchMarker != null) searchMarker?.remove()
                calFunction.toast("Doesn't found room",this@Maps2Activity)
            }
        }
    }
    fun markSearchRoom(roomPosition : LatLng,roomName : String){
        functionSearch("found")
        if (searchMarker == null){
            searchMarker = mMap.addMarker(MarkerOptions().position(roomPosition).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title(roomName))
        }else{
            searchMarker?.remove()
            searchMarker = mMap.addMarker(MarkerOptions().position(roomPosition).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title(roomName))
        }
    }

    fun navigationOperation(){
        val buff : String = editText_navigation.text.toString()
        button_navigation.setOnClickListener {
            search_font_2.visibility = View.GONE
            navigation_background.visibility = View.VISIBLE
        }
        navigation_back_to_search.setOnClickListener {
            navigation_background.visibility = View.GONE
            search_font_2.visibility = View.VISIBLE
        }
        button_set_navigation.setOnClickListener {
            calFunction.toast("Coming Soon",this@Maps2Activity)
        }
    }
    fun createLine(target : LatLng,name : String){
        val lineOption = PolylineOptions().color(Color.RED)
        lineOption.add(myLocation)
        when(name){
            "ZoneA" -> lineOption.add(LatLng(5.5,3.75))
            "ZoneB" -> lineOption.add(LatLng(7.5,8.0))
        }
        lineOption.add(target)
        if (polyLine == null){
            polyLine = mMap.addPolyline(lineOption)
        }else{
            polyLine?.remove()
            polyLine =  mMap.addPolyline(lineOption)
        }
        if (myLocation!=target){
            //update Location
            mHandler?.postDelayed({
                createLine(target,name)
            },5000)
        }else{
            calFunction.toast("XXXXX",this@Maps2Activity)
        }
//        mMap.addPolyline(PolylineOptions().geodesic(true).add(myLocation).add(target))
    }
    fun setPoint(){
        addPoint(LatLng(3.75,2.75)  , arrayOf(1,3))
        addPoint(LatLng(7.5,2.75)   , arrayOf(0,2))
        addPoint(LatLng(11.25,2.75) , arrayOf(1,4))
        addPoint(LatLng(3.75,5.5)   , arrayOf(0,6))
        addPoint(LatLng(7.5,5.5)    , arrayOf(2,5,7))

        addPoint(LatLng(11.25,5.5)  , arrayOf(4))
        addPoint(LatLng(3.75,8.25)  , arrayOf(3,7))
        addPoint(LatLng(7.5,8.25)   , arrayOf(4,6,8))
        addPoint(LatLng(11.25,8.25) , arrayOf(7))
    }
    fun addPoint(latLng: LatLng,path: Array<Int>){
        val point : PointOfLine = PointOfLine()
        point.PointOfLine(latLng,path)
        pointOfLine.add(point)
    }

    fun setBeaconInformation(){
        addBeaconDetail(LatLng(0.0,0.0),-64)
        addBeaconDetail(LatLng(15.0,0.0),-68)
        addBeaconDetail(LatLng(0.0,11.0),-68)
        addBeaconDetail(LatLng(15.0,11.0),-68)
    }
    fun addBeaconDetail(position: LatLng,power:Int){
        val detail : BeaconDetail = BeaconDetail()
        detail.Detail(position, power)
        beaconDetail.add(detail)
    }

    fun startScanner(){
        mHandler?.postDelayed({
            stopScanner()
            findMyLocation()
        },5000)
        mScanner?.startScan(null,scanSetting,leScanCallBack)
    }
    fun stopScanner(){
        mScanner?.stopScan(leScanCallBack)
        if (isMyLocation != null){
            isMyLocation?.remove()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        if (showMyLocation){
            mScanner?.stopScan(leScanCallBack)
        }
    }
}
