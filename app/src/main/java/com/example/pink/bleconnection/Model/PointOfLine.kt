package com.example.pink.bleconnection.Model

import com.google.android.gms.maps.model.LatLng
import java.util.*

class PointOfLine(position : LatLng,path : Array<Int>){
    private var local : LatLng = LatLng(0.0,0.0)
    private var nodePath : Array<Int> = arrayOf()
    init {
        this.local = position
        this.nodePath = path
    }

    fun getLocal(): LatLng{
        return local
    }
    fun getPath():Array<Int>{
        return nodePath
    }
    fun getPathWay():Int{
        val rand : Int = Random().nextInt(nodePath.size)
        return rand
    }
    fun setLocal(position: LatLng){
        this.local = position
    }
    fun setPath(path: Array<Int>){
        this.nodePath = path
    }
}