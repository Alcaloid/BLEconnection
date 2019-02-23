package com.example.pink.bleconnection.Map

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast

import com.example.pink.bleconnection.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.fragment_map.*

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mBluetoothManager : BluetoothManager
    private lateinit var mBluetoothAdapter : BluetoothAdapter
    private lateinit var mScanner : BluetoothLeScanner
    private lateinit var mHandler: Handler
    private lateinit var scanSetting : ScanSettings

    private var showMyLocation : Boolean = false
    private var showNavigation : Boolean = false
    private var searchMarker : Marker? = null
    private var myLocation : LatLng? = null
    private var polyLine : Polyline? = null
    private var placeName : ArrayList<String> = arrayListOf(
            "ZoneA","ZoneB","ZoneC","ZoneD"
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapview) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mBluetoothManager = activity?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = mBluetoothManager.adapter
        mHandler = Handler()
        searchOperation(view.context)
    }
    override fun onMapReady(googleMap: GoogleMap) {
        val testingRoom = LatLngBounds(
                LatLng(0.0, 0.0),
                LatLng(60.0,35.0))
        val mapGroundOverLay = GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.floor11))
                .positionFromBounds(testingRoom).zIndex(1f)
        val locationZoom = LatLng(7.5,5.5)
        val cameraTraget = LatLngBounds(
                LatLng(-0.01,-0.01), LatLng(90.001, 90.001))
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NONE
        mMap.addGroundOverlay(mapGroundOverLay)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationZoom,10f))
        mMap.setLatLngBoundsForCameraTarget(cameraTraget)
        mMap.uiSettings.isRotateGesturesEnabled = false
        mMap.uiSettings.isMapToolbarEnabled = false
        mMap.setOnCameraChangeListener {
            val maxZoom = 12.0f;
            val minZoom = 5.0f
            if (mMap.cameraPosition.zoom > maxZoom){
                mMap.animateCamera(CameraUpdateFactory.zoomTo(maxZoom))
            }else if(mMap.cameraPosition.zoom < minZoom){
                mMap.animateCamera(CameraUpdateFactory.zoomTo(minZoom))
            }
        }

//        checkPermission()
    }
    override fun onDestroy() {
        super.onDestroy()
        if (showMyLocation){
            mScanner.stopScan(leScanCallBack)
        }
    }

    private fun checkPermission(){
        if(activity?.packageManager!!.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            if (!mBluetoothAdapter.isEnabled){
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, 1)
            }
            Dexter.withActivity(context as Activity)
                    .withPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    .withListener(object : PermissionListener {
                        override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                            mScanner = mBluetoothAdapter.bluetoothLeScanner
                            scanSetting = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
                            startScanner()
                        }
                        override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                            toast("Navigator system need location permission")
                        }
                        override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                            token.continuePermissionRequest()
                        }
                    }).check()
        }else{
            toast("This Device can't support BLE")
        }
    }
    private var leScanCallBack = object : ScanCallback(){
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            println("GetData->"+result.device.name+" Address:"+result.device.address)
            if(result.device.name == "RL0"){
//                beaconSignal[0].add(result.rssi)
            }else if (result.device.name == "RL1"){
//                beaconSignal[1].add(result.rssi)
            }else if (result.device.name == "RL2"){
//                beaconSignal[2].add(result.rssi)
            }else if (result.device.name == "RL3"){
//                beaconSignal[3].add(result.rssi)
            }
        }
    }
    fun startScanner(){
        mHandler.postDelayed({
            stopScanner()
            //findMyLocation()
        },5000)
        mScanner.startScan(null,scanSetting,leScanCallBack)
    }
    fun stopScanner(){
        mScanner.stopScan(leScanCallBack)
    }

    private fun searchOperation(context: Context){
        var buff : String = ""
        val adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, placeName)
        mListPlaceName.setAdapter(adapter)
        button_map_start_search.setOnClickListener {
            searchBackground.setBackgroundColor(resources.getColor(R.color.angel_white))
            search_font.visibility = View.GONE
            search_backend.visibility = View.VISIBLE
            mListPlaceName.visibility = View.VISIBLE
        }
        button_search_closer.setOnClickListener {
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
            editText_search_place.setText(adapter.getItem(position))
        }
    }
    private fun checkSearch(string: String){
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
    private fun markSearchRoom(roomPosition : LatLng,roomName : String){
        if (searchMarker == null){
            searchMarker = mMap.addMarker(MarkerOptions().position(roomPosition).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title(roomName))
        }else{
            searchMarker?.remove()
            searchMarker = mMap.addMarker(MarkerOptions().position(roomPosition).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title(roomName))
        }
        if (myLocation != null){
            createLine(roomPosition,roomName)
        }
    }
    private fun createLine(target : LatLng,name : String){
        val lineOption = PolylineOptions().color(Color.RED)
        lineOption.add(myLocation)
        //wait for edit
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
//        mMap.addPolyline(PolylineOptions().geodesic(true).add(myLocation).add(target))
    }

    fun toast(text : String){
        Toast.makeText(context,text, Toast.LENGTH_SHORT).show()
    }
}
