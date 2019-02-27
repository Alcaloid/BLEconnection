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
import com.example.pink.bleconnection.Model.PointOfLine
import com.example.pink.bleconnection.Model.RoomDetail

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

class IndoorMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mBluetoothManager : BluetoothManager
    private lateinit var mBluetoothAdapter : BluetoothAdapter
    private lateinit var mScanner : BluetoothLeScanner
    private lateinit var mHandler: Handler
    private lateinit var scanSetting : ScanSettings

    private var pointOfLine : ArrayList<PointOfLine> = arrayListOf()
    private var roomDetail : ArrayList<RoomDetail> = arrayListOf()

    private var showMyLocation : Boolean = false
    private var showNavigation : Boolean = false
    private var searchMarker : Marker? = null
    private var myLocation : LatLng? = null
    private var polyLine : Polyline? = null
    private var placeName : ArrayList<String> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapview) as SupportMapFragment
        mapFragment.getMapAsync(this)
        searchOperation(view.context)
        mBluetoothManager = activity?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = mBluetoothManager.adapter
        mHandler = Handler()
    }
    override fun onMapReady(googleMap: GoogleMap) {
        val testingRoom = LatLngBounds(
                LatLng(0.0, 0.0),
                LatLng(70.0,36.0))
        val mapGroundOverLay = GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.seniortesting))
                .positionFromBounds(testingRoom).zIndex(1f)
        val locationZoom = LatLng(7.5,5.5)
        val cameraTraget = LatLngBounds(
                LatLng(-0.01,-0.01), LatLng(90.001, 90.001))
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NONE
        mMap.addGroundOverlay(mapGroundOverLay)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationZoom,4f))
        mMap.setLatLngBoundsForCameraTarget(cameraTraget)
        mMap.uiSettings.isRotateGesturesEnabled = false
        mMap.uiSettings.isMapToolbarEnabled = false
        mMap.setOnCameraChangeListener {
            val maxZoom = 12.0f;
            val minZoom = 3.0f
            if (mMap.cameraPosition.zoom > maxZoom){
                mMap.animateCamera(CameraUpdateFactory.zoomTo(maxZoom))
            }else if(mMap.cameraPosition.zoom < minZoom){
                mMap.animateCamera(CameraUpdateFactory.zoomTo(minZoom))
            }
        }
        setPoint()
        setRoomDetail()
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
                toast("Set string error")
            }
        }
    }
    fun searchOperation(context: Context){
        val adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, placeName)
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
        for (i in roomDetail){
            if (buff == i.getRoomName().toLowerCase()){
                markSearchRoom(i.getRoomPosition(),i.getRoomName())
                break
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
    fun setRoomDetail(){
        addRoomDetail("1101:Lab", LatLng(63.0,5.0))
        addRoomDetail("1102:Graduation Common Room",LatLng(63.0,11.0))
        addRoomDetail("Toilet Man(1)",LatLng(65.0,31.0))
        addRoomDetail("Toilet Woman(1)",LatLng(61.0,31.0))
        addRoomDetail("1111/1:iNeng Lab",LatLng(55.0,7.0))
        addRoomDetail("1111/2:Lab",LatLng(55.0,13.0))
        addRoomDetail("CPE 1112:Computer room1",LatLng(47.0,10.0))
        addRoomDetail("CPE 1113:Computer room2",LatLng(39.0,10.0))
        addRoomDetail("CPE 1114:Class room",LatLng(31.0,10.0))
        addRoomDetail("CPE 1115:Class room",LatLng(23.0,10.0))
        addRoomDetail("CPE 1116:Class room",LatLng(15.0,10.0))
        addRoomDetail("CPE 1117:Lab",LatLng(55.0,28.0))
        addRoomDetail("CPE 1118:Lab embedded",LatLng(47.0,28.0))
        addRoomDetail("CPE 1119:Lab electronic",LatLng(39.0,28.0))
        addRoomDetail("CPE 1120:Lab network",LatLng(31.0,28.0))
        addRoomDetail("CPE 1121:Class room",LatLng(23.0,28.0))
        addRoomDetail("CPE 1122:Server room&IT Admin",LatLng(15.0,28.0))
        addRoomDetail("Toilet Man(2)",LatLng(9.5,5.0))
        addRoomDetail("Toilet Woman(2)",LatLng(4.5,5.0))
        addRoomDetail("1130:Cast Lab",LatLng(5.5,28.0))
        addRoomDetail("1131:Cast Lab",LatLng(5.5,32.0))
    }
    fun addRoomDetail(name:String,position:LatLng){
        val room : RoomDetail = RoomDetail()
        room.RoomDetail(name,position)
        placeName.add(name)
        //mMap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(position).title(name))
        roomDetail.add(room)
    }
    fun setPoint(){

    }
    fun addPoint(latLng: LatLng,path: Array<Int>){
        val point : PointOfLine = PointOfLine()
        point.PointOfLine(latLng,path)
        pointOfLine.add(point)
    }
    fun toast(text : String){
        Toast.makeText(context,text, Toast.LENGTH_SHORT).show()
    }
}
