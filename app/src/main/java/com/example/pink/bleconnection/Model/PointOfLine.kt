package com.example.pink.bleconnection.Model

import com.google.android.gms.maps.model.LatLng

class PointOfLine(){
    private var local : LatLng = LatLng(0.0,0.0)
    private var nodePath : Array<Int> = arrayOf()

    fun PointOfLine(position : LatLng,path : Array<Int>){
        this.local = position
        this.nodePath = path
    }
    fun getLocal(): LatLng{
        return local
    }
    fun getPath():Array<Int>{
        return nodePath
    }
    fun setLocal(position: LatLng){
        this.local = position
    }
    fun setPath(path: Array<Int>){
        this.nodePath = path
    }
}