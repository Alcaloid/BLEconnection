package com.example.pink.bleconnection.Model

import com.google.android.gms.maps.model.LatLng

class RoomDetail{
    private var roomName : String = ""
    private var roomPosition : LatLng = LatLng(0.0,0.0)
    fun RoomDetail(name:String,position:LatLng){
        this.roomName = name
        this.roomPosition = position
    }
    fun setRoomName(name: String){
        this.roomName = name
    }
    fun setRoomPosition(position: LatLng){
        this.roomPosition = position
    }
    fun getRoomName():String{
        return this.roomName
    }
    fun getRoomPosition():LatLng{
        return this.roomPosition
    }
}