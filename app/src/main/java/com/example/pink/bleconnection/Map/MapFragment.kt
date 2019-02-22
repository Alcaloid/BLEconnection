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
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.example.pink.bleconnection.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mBluetoothManager : BluetoothManager
    private lateinit var mBluetoothAdapter : BluetoothAdapter
    private lateinit var mScanner : BluetoothLeScanner
    private lateinit var mHandler: Handler
    private lateinit var scanSetting : ScanSettings

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapview) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationZoom,5f))
        mMap.setLatLngBoundsForCameraTarget(cameraTraget)
        mMap.uiSettings.isRotateGesturesEnabled = false
        mMap.uiSettings.isMapToolbarEnabled = false
        mMap.setOnCameraChangeListener {
            val maxZoom = 10.0f;
            val minZoom = 5.0f
            if (mMap.cameraPosition.zoom > maxZoom){
                mMap.animateCamera(CameraUpdateFactory.zoomTo(maxZoom))
            }else if(mMap.cameraPosition.zoom < minZoom){
                mMap.animateCamera(CameraUpdateFactory.zoomTo(minZoom))
            }
        }
        checkPermission()
    }

    private fun checkPermission(){
        mBluetoothManager = activity?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = mBluetoothManager.adapter
        mHandler = Handler()
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
            //stopScanner()
            //findMyLocation()
        },5000)
        mScanner.startScan(null,scanSetting,leScanCallBack)
    }
    fun stopScanner(){
        mScanner.stopScan(leScanCallBack)
    }

    override fun onDestroy() {
        super.onDestroy()
        mScanner.stopScan(leScanCallBack)
    }
    fun toast(text : String){
        Toast.makeText(context,text, Toast.LENGTH_SHORT).show()
    }
}
