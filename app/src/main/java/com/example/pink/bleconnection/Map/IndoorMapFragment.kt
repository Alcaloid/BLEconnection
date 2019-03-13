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
import com.example.pink.bleconnection.Model.RoomNameAdapter

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
import java.util.*
import kotlin.collections.ArrayList

class IndoorMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mBluetoothManager : BluetoothManager
    private lateinit var mBluetoothAdapter : BluetoothAdapter
    private lateinit var mScanner : BluetoothLeScanner
    private lateinit var mHandler: Handler
    private lateinit var scanSetting : ScanSettings

    private var pointOfLine : ArrayList<PointOfLine> = arrayListOf(

    )
    private var roomDetail : ArrayList<RoomDetail> = arrayListOf(
            RoomDetail("1101:Lab", LatLng(63.0,2.0)) ,
            RoomDetail("1102:Graduation Common Room",LatLng(63.0,8.0)) ,
            RoomDetail("Toilet Man(1)",LatLng(65.0,28.0)) ,
            RoomDetail("Toilet Woman(1)",LatLng(61.0,28.0)) ,
            RoomDetail("1111/1:iNeng Lab",LatLng(55.0,4.0)) ,
            RoomDetail("1111/2:Lab",LatLng(55.0,10.0)) ,
            RoomDetail("CPE 1112:Computer room1",LatLng(47.0,7.0)) ,
            RoomDetail("CPE 1113:Computer room2",LatLng(39.0,7.0)) ,
            RoomDetail("CPE 1114:Class room",LatLng(31.0,7.0)) ,
            RoomDetail("CPE 1115:Class room",LatLng(23.0,7.0)) ,
            RoomDetail("CPE 1116:Class room",LatLng(15.0,7.0)) ,
            RoomDetail("CPE 1117:Lab",LatLng(55.0,25.0)) ,
            RoomDetail("CPE 1118:Lab embedded",LatLng(47.0,25.0)) ,
            RoomDetail("CPE 1119:Lab electronic",LatLng(39.0,25.0)) ,
            RoomDetail("CPE 1120:Lab network",LatLng(31.0,25.0)) ,
            RoomDetail("CPE 1121:Class room",LatLng(23.0,25.0)) ,
            RoomDetail("CPE 1122:Server room&IT Admin",LatLng(15.0,25.0)) ,
            RoomDetail("Toilet Woman(2)",LatLng(9.5,2.0)) ,
            RoomDetail("Toilet Man(2)",LatLng(4.5,2.0)) ,
            RoomDetail("1130:Cast Lab",LatLng(5.5,25.0)) ,
            RoomDetail("1131:Cast Lab",LatLng(5.5,29.0))
    )

    private var showMyLocation : Boolean = false
    private var showNavigation : Boolean = false
    private var searchMarker : Marker? = null
    private var myLocation : LatLng? = null
    private var polyLine : Polyline? = null

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
                .positionFromBounds(testingRoom).zIndex(0f)
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
        //setPoint()
        //Test
        myLocation = LatLng(9.0,18.0)
        switchMyLocal()
        mMap.addMarker(MarkerOptions().position(myLocation!!).title("MyLocal"))
        navigationOperation()
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
                    checkSearch(buff,"Search")
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
        val roomAdapter : RoomNameAdapter = RoomNameAdapter(roomDetail,context)
        mListPlaceName.setAdapter(roomAdapter)
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
                roomAdapter.filter.filter(str)
            }
        })
        button_search.setOnClickListener {
            functionSearch("search")
        }
        button_search_delete_text.setOnClickListener {
            functionSearch("close")
        }
        mListPlaceName.setOnItemClickListener { parent, view, position, id ->
            editText_search_place_2.setText(roomAdapter.getItem(position)!!.getRoomName())
        }
    }
    fun checkSearch(string: String,option:String){
        val buff = string.toLowerCase()
        for (i in roomDetail){
            if (buff == i.getRoomName().toLowerCase()){
                if (option == "Search"){
                    markSearchRoom(i.getRoomPosition(),i.getRoomName(),option)
                }else if (option == "Navigation"){
                    markSearchRoom(i.getRoomPosition(),i.getRoomName(),option)
                    println("Mark Pass")
                    createLine(i.getRoomPosition(),i.getRoomName())
                }
                break
            }
        }
    }
    fun markSearchRoom(roomPosition : LatLng,roomName : String,option: String){
        if (option == "Search"){
            functionSearch("found")
        }
        if (searchMarker == null){
            searchMarker = mMap.addMarker(MarkerOptions().position(roomPosition).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title(roomName))
        }else{
            searchMarker?.remove()
            searchMarker = mMap.addMarker(MarkerOptions().position(roomPosition).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title(roomName))
        }
    }

    fun switchMyLocal(){
        var near : Double = 100000.0
        var distance : Double = 0.0
        var point : LatLng
        var nearLocal : LatLng = LatLng(0.0,0.0)
        for(i in pointOfLine.indices){
            point = pointOfLine[i].getLocal()
            distance = distanceFromPoint(myLocation!!.latitude,point.latitude,myLocation!!.longitude,point.longitude)
            if (distance < near){
                near = distance
                nearLocal = point
            }
        }
        myLocation = nearLocal
    }
    fun distanceFromPoint(X1:Double,X2:Double,Y1:Double,Y2:Double):Double{
        val distance : Double = Math.sqrt(Math.pow(X1-X2,2.0)+Math.pow(Y1-Y2,2.0))
        return distance
    }

    fun navigationOperation(){
        button_navigation.setOnClickListener {
            navigation_background.visibility = View.VISIBLE
            search_font_2.visibility = View.GONE
            mListPlaceName.visibility = View.VISIBLE
        }
        navigation_back_to_search.setOnClickListener {
            if (polyLine!=null){
                polyLine?.remove()
            }
            if (searchMarker!=null){
                searchMarker?.remove()
            }
            mListPlaceName.visibility = View.GONE
            navigation_background.visibility = View.GONE
            search_font_2.visibility = View.VISIBLE
        }
        button_set_navigation.setOnClickListener {
            mListPlaceName.visibility = View.GONE
//            toast("Coming Soon")

            val buff : String = editText_navigation.text.toString()
            checkSearch(buff,"Navigation")
        }
    }
    fun createLine(target : LatLng,name : String){
        val lineOption = PolylineOptions().color(Color.RED)
        var position : LatLng = myLocation!!
        var currentPosition : Int = 0
        var prePosition : Int? = null
        val arrayPath : ArrayList<LatLng> = arrayListOf()
        var pathOfPoint : Array<Int> = arrayOf()
        var count : Int = 0
        var rand : Int
        var point : Int
//        println("Start Position:"+position)
        if (position == target) lineOption.add(position)
        else{
            while (position != target) {
                println("Position:" + position)
                arrayPath.add(position)
                for (i in pointOfLine.indices) {
                    if (position == pointOfLine[i].getLocal()) {
                        pathOfPoint = pointOfLine[i].getPath()
                        currentPosition = i
                        break
                    }
                }
                rand = Random().nextInt(pathOfPoint.size)
                point = pathOfPoint[rand]
                if (prePosition==null){
                    position = pointOfLine[point].getLocal()
                    prePosition = currentPosition
                }else{
                    count = 0
                    while (point == prePosition){
                        rand = Random().nextInt(pathOfPoint.size)
                        point = pathOfPoint[rand]
                        count += 1
                        if (count == 10){
                            break
                        }
                    }
                    if (point != prePosition){
                        position = pointOfLine[point].getLocal()
                        prePosition = currentPosition
                    }else break
                    /*if (count == 10){
                        position = myLocation!!
                        arrayPath.clear()
                    }else{
                        position = pointOfLine[point].getLocal()
                        prePosition = currentPosition
                    }*/
                }
            }
        }
//        println("Array Size"+arrayPath.size)
        if (arrayPath.size != 0){
            for(i in arrayPath){
//                println("path:"+i)
                lineOption.add(i)
            }
        }
        lineOption.add(target)
        /*lineOption.add(myLocation)
        lineOption.add(target)*/
        if (polyLine == null){
            polyLine = mMap.addPolyline(lineOption)
        }else{
            polyLine?.remove()
            polyLine =  mMap.addPolyline(lineOption)
        }
    }

    /*fun setPoint(){
        addPoint(LatLng(0.0,0.0), arrayOf(0)) // i forgot array start 0 T-T
        //1-5
        addPoint(LatLng(7.0,18.0), arrayOf(2))
        addPoint(LatLng(12.0,18.0), arrayOf(1,3,8))
        addPoint(LatLng(12.0,12.0), arrayOf(2,4))
        addPoint(LatLng(9.5,12.0), arrayOf(3,5,6))
        addPoint(LatLng(4.5,12.0), arrayOf(4,7))
        //6-10
        addPoint(LatLng(9.5,5.0), arrayOf(4)) //toilet woman2
        addPoint(LatLng(4.5,5.0), arrayOf(5)) //toilet man2
        addPoint(LatLng(12.0,28.0), arrayOf(2,9,10,40))
        addPoint(LatLng(15.0,28.0), arrayOf(8)) //CPE 1122:Server room&IT Admin
        addPoint(LatLng(5.5,28.0), arrayOf(8)) //1130:Cast Lab
        //11-15
        addPoint(LatLng(5.5,32.0), arrayOf(40)) //1131:Cast Lab
        addPoint(LatLng(15.0,18.0), arrayOf(2,13,14))
        addPoint(LatLng(15.0,10.0), arrayOf(12)) //CPE 1116:Class room
        addPoint(LatLng(19.0,18.0), arrayOf(12,15,16,17))
        addPoint(LatLng(23.0,10.0), arrayOf(14))
        //16-20
        addPoint(LatLng(23.0,28.0), arrayOf(14)) //CPE 1121:Class room
        addPoint(LatLng(31.0,18.0), arrayOf(14,18,19,20))
        addPoint(LatLng(31.0,10.0), arrayOf(17)) // 1114:Class room
        addPoint(LatLng(31.0,28.0), arrayOf(17)) //CPE 1120:Lab network
        addPoint(LatLng(39.0,18.0), arrayOf(17,21,22,23))
        //21-25
        addPoint(LatLng(39.0,10.0), arrayOf(20)) //com 2
        addPoint(LatLng(39.0,28.0), arrayOf(20)) //ele lab
        addPoint(LatLng(47.0,18.0), arrayOf(20,24,25,26))
        addPoint(LatLng(47.0,10.0), arrayOf(23))
        addPoint(LatLng(47.0,28.0), arrayOf(23))
        //26-30
        addPoint(LatLng(55.0,18.0), arrayOf(23,27,28,29))
        addPoint(LatLng(55.0,13.0), arrayOf(26)) //lab
        addPoint(LatLng(55.0,28.0), arrayOf(26)) //lab
        addPoint(LatLng(57.5,18.0), arrayOf(26,30,31,35))
        addPoint(LatLng(63.0,18.0), arrayOf(29))
        //31-35
        addPoint(LatLng(57.5,8.0), arrayOf(29,32,41))
        addPoint(LatLng(63.0,11.0), arrayOf(31)) //1102:Graduation Common Room
        addPoint(LatLng(63.0,5.0), arrayOf(41)) //Lab
        addPoint(LatLng(55.0,7.0), arrayOf(41)) //1111/1:iNeng Lab
        addPoint(LatLng(57.5,25.0), arrayOf(29,36))
        //36-41
        addPoint(LatLng(61.0,25.0), arrayOf(35,37,38))
        addPoint(LatLng(65.0,25.0), arrayOf(36,39))
        addPoint(LatLng(61.0,31.0), arrayOf(36)) // toilet woman1
        addPoint(LatLng(65.0,31.0), arrayOf(37)) // totlet man1
        addPoint(LatLng(12.0,31.0), arrayOf(8,11))
        addPoint(LatLng(57.5,5.0), arrayOf(31,33,34))
    }
    fun addPoint(latLng: LatLng,path: Array<Int>){
        val point : PointOfLine = PointOfLine()
//        mMap.addMarker(MarkerOptions().position(latLng).title(latLng.toString()))
        point.PointOfLine(latLng,path)
        pointOfLine.add(point)
    }*/
    fun toast(text : String){
        Toast.makeText(context,text, Toast.LENGTH_SHORT).show()
    }
}
