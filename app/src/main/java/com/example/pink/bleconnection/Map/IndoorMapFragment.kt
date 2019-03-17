package com.example.pink.bleconnection.Map

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanSettings
import android.content.Context
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
    //point to mark line for navigation
    private var pointOfLine : ArrayList<PointOfLine> = arrayListOf(
            //ref by point on pic point ver2
            //0-4
            PointOfLine(LatLng(9.5,15.0), arrayOf(1)),
            PointOfLine(LatLng(16.5,15.0), arrayOf(0,2,8,13)),
            PointOfLine(LatLng(16.5,8.0), arrayOf(1,3,4)),
            PointOfLine(LatLng(16.5,2.0), arrayOf(2)),
            PointOfLine(LatLng(13.0,8.0), arrayOf(2,5,6)),
            //5-9
            PointOfLine(LatLng(6.0,8.0), arrayOf(4,7)),
            PointOfLine(LatLng(13.0,5.0), arrayOf(4)), //Toilet woman 2
            PointOfLine(LatLng(6.0,5.0), arrayOf(5)),  //Toilet Man 2
            PointOfLine(LatLng(16.5,24.0), arrayOf(1,9,10,11)),
            PointOfLine(LatLng(16.5,28.0), arrayOf(8,12)),
            //10-14
            PointOfLine(LatLng(9.0,24.0), arrayOf(8)), //CPE 1130:Cast Lab
            PointOfLine(LatLng(22.0,24.0), arrayOf(8)), //CPE 1122:Server room&IT Admin
            PointOfLine(LatLng(9.0,28.0), arrayOf(9)), //CPE 1131:Cast Lab
            PointOfLine(LatLng(23.0,15.0), arrayOf(1,14,21)),
            PointOfLine(LatLng(31.0,15.0), arrayOf(13,15,22,27)),
            //15-19
            PointOfLine(LatLng(40.0,15.0), arrayOf(14,16,23,28)),
            PointOfLine(LatLng(48.0,15.0), arrayOf(15,17,24,29)),
            PointOfLine(LatLng(55.0,15.0), arrayOf(16,18,25,30)),
            PointOfLine(LatLng(60.0,15.0), arrayOf(17,19,26,31)),
            PointOfLine(LatLng(63.0,15.0), arrayOf(18,20,32,38)),
            //20-24
            PointOfLine(LatLng(66.0,15.0), arrayOf(19)),
            PointOfLine(LatLng(22.0,8.0), arrayOf(13)), //CPE 1116:Class room
            PointOfLine(LatLng(31.0,8.0), arrayOf(14)), //CPE 1115:Class room
            PointOfLine(LatLng(40.0,8.0), arrayOf(15)), //CPE 1114:Class room
            PointOfLine(LatLng(48.0,8.0), arrayOf(16)), //CPE 1113:Computer room2
            //25-29
            PointOfLine(LatLng(55.0,7.0), arrayOf(17)),  //CPE 1112:Computer room1
            PointOfLine(LatLng(60.0,10.0), arrayOf(18)), //Lab 1111/2
            PointOfLine(LatLng(31.0,23.0), arrayOf(14)), //CPE 1121:Class room
            PointOfLine(LatLng(40.0,23.0), arrayOf(15)), //CPE 1120:Lab network
            PointOfLine(LatLng(48.0,23.0), arrayOf(16)), //CPE 1119:Lab electronic
            //30-34
            PointOfLine(LatLng(55.0,23.0), arrayOf(17)), //CPE 1118:Lab embedded
            PointOfLine(LatLng(60.0,23.0), arrayOf(18)), //CPE 1117:Lab
            PointOfLine(LatLng(63.0,22.0), arrayOf(19,33,34)),
            PointOfLine(LatLng(63.0,27.5), arrayOf(32)),
            PointOfLine(LatLng(64.5,22.0), arrayOf(32,35,37)),
            //35-39
            PointOfLine(LatLng(67.5,22.0), arrayOf(34,36)),
            PointOfLine(LatLng(67.5,25.0), arrayOf(35)), //Toilet man 1
            PointOfLine(LatLng(64.5,25.0), arrayOf(34)), //Toilet woman 1
            PointOfLine(LatLng(63.0,7.0), arrayOf(19,39,42)),
            PointOfLine(LatLng(63.0,3.0), arrayOf(38,40,41)),
            //40-42
            PointOfLine(LatLng(60.0,3.0), arrayOf(39)), //CPE 1111/1:iNeng La
            PointOfLine(LatLng(66.0,3.0), arrayOf(39)), //CPE 1101:Lab
            PointOfLine(LatLng(66.0,7.0), arrayOf(38))  //CPE 1102:Graduation Common Room
    )
    //location room
    private var roomDetail : ArrayList<RoomDetail> = arrayListOf(
            RoomDetail("CPE 1101:Lab", LatLng(66.0,3.0)) ,
            RoomDetail("CPE 1102:Graduation Common Room",LatLng(66.0,7.0)) ,
            RoomDetail("Toilet Man(1)",LatLng(67.5,25.0)) ,
            RoomDetail("Toilet Woman(1)",LatLng(64.5,25.0)) ,
            RoomDetail("CPE 1111/1:iNeng Lab",LatLng(60.0,4.0)) ,
            RoomDetail("CPE 1111/2:Lab",LatLng(60.0,10.0)) ,
            RoomDetail("CPE 1112:Computer room1",LatLng(55.0,7.0)) ,
            RoomDetail("CPE 1113:Computer room2",LatLng(48.0,8.0)) ,
            RoomDetail("CPE 1114:Class room",LatLng(40.0,8.0)) ,
            RoomDetail("CPE 1115:Class room",LatLng(31.0,8.0)) ,
            RoomDetail("CPE 1116:Class room",LatLng(22.0,8.0)) ,
            RoomDetail("CPE 1117:Lab",LatLng(60.0,23.0)) ,
            RoomDetail("CPE 1118:Lab embedded",LatLng(55.0,23.0)) ,
            RoomDetail("CPE 1119:Lab electronic",LatLng(48.0,23.0)) ,
            RoomDetail("CPE 1120:Lab network",LatLng(40.0,23.0)) ,
            RoomDetail("CPE 1121:Class room",LatLng(31.0,23.0)) ,
            RoomDetail("CPE 1122:Server room&IT Admin",LatLng(22.0,24.0)) ,
            RoomDetail("Toilet Woman(2)",LatLng(13.0,5.0)) ,
            RoomDetail("Toilet Man(2)",LatLng(6.0,5.0)) ,
            RoomDetail("CPE 1130:Cast Lab",LatLng(9.0,24.0)) ,
            RoomDetail("CPE 1131:Cast Lab",LatLng(9.0,28.0))
    )
    private var roomNameArray : ArrayList<String> = arrayListOf(
            "CPE 1101:Lab",
            "CPE 1102:Graduation Common Room",
            "CPE 1111/1:iNeng Lab",
            "CPE 1111/2:Lab",
            "CPE 1112:Computer room1",
            "CPE 1113:Computer room2",
            "CPE 1114:Class room",
            "CPE 1115:Class room",
            "CPE 1116:Class room",
            "CPE 1117:Lab",
            "CPE 1118:Lab embedded",
            "CPE 1119:Lab electronic",
            "CPE 1120:Lab network",
            "CPE 1121:Class room",
            "CPE 1122:Server room&IT Admin",
            "CPE 1130:Cast Lab",
            "CPE 1131:Cast Lab",
            "Toilet Man(1)",
            "Toilet Man(2)",
            "Toilet Woman(1)",
            "Toilet Woman(2)"
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
        searchViewOption(view.context)
        mBluetoothManager = activity?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = mBluetoothManager.adapter
        mHandler = Handler()
    }
    override fun onMapReady(googleMap: GoogleMap) {
        val testingRoom = LatLngBounds(
                LatLng(0.0, 0.0),
                LatLng(70.0,30.0))
        val mapGroundOverLay = GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.floor11v2))
                .positionFromBounds(testingRoom).zIndex(0f)
        val locationZoom = LatLng(7.5,5.5)
        val cameraTraget = LatLngBounds(
                LatLng(-0.01,-0.01), LatLng(70.001, 30.001))
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
        mMap.setOnMapClickListener (object : GoogleMap.OnMapClickListener{
            override fun onMapClick(latLng: LatLng?) {
                toast("LagLng is "+latLng)
                println("LagLng:"+latLng)
            }

        })
        //setPoint()
        //Test
        myLocation = LatLng(9.0,18.0)
        switchMyLocal()
        mMap.addMarker(MarkerOptions().position(myLocation!!).title("MyLocal"))
        navigationViewOption()
    }
    //Test Point
    fun testerPoint(){
        var count = 0
        for (i in pointOfLine){
            mMap.addMarker(MarkerOptions().position(i.getLocal()).title(count.toString()))
            count += 1
        }
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
    fun searchViewOption(context: Context){
        //val roomAdapter : RoomNameAdapter = RoomNameAdapter(roomDetail,context)
        val adapter : ArrayAdapter<String> = ArrayAdapter(context,android.R.layout.simple_list_item_1,roomNameArray)
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
    fun checkSearch(string: String,option:String){
        val buff = string.toLowerCase()
        for (i in roomDetail){
            if (buff == i.getRoomName().toLowerCase()){
                if (option == "Search"){
                    markSearchRoom(i.getRoomPosition(),i.getRoomName(),option)
                }else if (option == "Navigation"){
                    markSearchRoom(i.getRoomPosition(),i.getRoomName(),option)
//                    toast("This system coming soon")
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

    fun navigationViewOption(){
        button_navigation.setOnClickListener {
            if (myLocation != null){
                navigation_background.visibility = View.VISIBLE
                search_font_2.visibility = View.GONE
                mListPlaceName.visibility = View.VISIBLE
            }else{
                toast("Cannot find user location")
            }
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
        var pathArray : ArrayList<LatLng> = arrayListOf()
        pathArray = burnFunction(target)
        for (i in pathArray){
            println("Point of path is "+i)
            lineOption.add(i)
        }
        if (polyLine == null){
            polyLine = mMap.addPolyline(lineOption)
        }else{
            polyLine?.remove()
            polyLine =  mMap.addPolyline(lineOption)
        }
    }

    fun burnFunction(target: LatLng):ArrayList<LatLng>{
        val myPath : ArrayList<LatLng> = arrayListOf()
        var currentPosition : LatLng = myLocation!!
        var currentPointPosition : Int? = null
        var previousPointPosition : Int? = null
        val myPathWay : ArrayList<Int> = arrayListOf()
        var pathWayChoice : Array<Int> = arrayOf()
        val pathWayDeadEnd : ArrayList<Int> = arrayListOf()
        var randomNumber : Int = 0 //default way is 0(in array XD)
        var isNewRandom : Boolean = false
        var isDeadEnd : Boolean = false
        var count : Int = 0

        //Find current location on point
        for (item in pointOfLine.indices){
            if (currentPosition == pointOfLine[item].getLocal()) {
                currentPointPosition = item
                break
            }
        }
        while (currentPosition!=target){
            isDeadEnd = false
            //add current position
            if (!isNewRandom){
                myPath.add(currentPosition)
                myPathWay.add(currentPointPosition!!)
            }
            isNewRandom = false
            //get path way
            pathWayChoice = pointOfLine[currentPointPosition!!].getPath() //if mylocation is set on point
            //random the way to walk
            randomNumber = Random().nextInt(pathWayChoice.size) //Random is random number on array ex.[10,20,30] random 0 not 10 it's 0
            //if you start walk,walk it
            if (previousPointPosition==null){
                previousPointPosition = currentPointPosition
                currentPointPosition = pathWayChoice[randomNumber]
                currentPosition = pointOfLine[pathWayChoice[randomNumber]].getLocal()
            }else{
                //Check don't walk the previous way
                //If same previousWay pass it to new random
                if (pathWayChoice[randomNumber]!= previousPointPosition){
                    if (!pathWayDeadEnd.isEmpty()){
                        //If dead end not empty check path
                        for (i in pathWayDeadEnd){
                            if (pathWayChoice[randomNumber]==i){
                                isDeadEnd = true
                            }
                        }
                    }
                    if (isDeadEnd){
                        count = 0
                        //Check is all path way is dead end
                        for (i in pathWayChoice){
                            for (j in pathWayDeadEnd){
                                if (i==j){
                                    count += 1
                                }
                            }
                        }
                        //pathWayChoice.size-1 is remove the previous way
                        if (count == pathWayChoice.size-1){
                           //is all paths are dead end
                            //pathWayDeadEnd.clear() //save size of array
                            pathWayDeadEnd.add(currentPointPosition)
                            //walk back
                            //remove current position
                            myPath.remove(currentPosition)
                            myPathWay.remove(currentPointPosition)
                            //Start walk back to previous point
                            currentPointPosition = previousPointPosition
                            currentPosition = pointOfLine[previousPointPosition].getLocal()
                            previousPointPosition = myPathWay[myPathWay.size-2]
                            isNewRandom = true
                        }else{
                            //this path is dead end don't go~~~~
                            isNewRandom = true
                        }
                    }else{
                        previousPointPosition = currentPointPosition
                        currentPointPosition = pathWayChoice[randomNumber]
                        currentPosition = pointOfLine[pathWayChoice[randomNumber]].getLocal()
                    }
                }else if ((pathWayChoice[randomNumber]==previousPointPosition)&&(pathWayChoice.size==1)){
                    println("Dead End")
                    //If dead end go back
                    myPath.remove(currentPosition)
                    myPathWay.remove(currentPointPosition)
                    pathWayDeadEnd.add(currentPointPosition)
                    currentPointPosition = previousPointPosition
                    currentPosition = pointOfLine[previousPointPosition].getLocal()
                    previousPointPosition = myPathWay[myPathWay.size-2] //size-1 is current point
                    isNewRandom = true
                }else{
                    //new random
                    isNewRandom = true
                }
            }
        }
        //add target
        myPath.add(currentPosition)
        return myPath
    }
    fun toast(text : String){
        Toast.makeText(context,text, Toast.LENGTH_SHORT).show()
    }
}
