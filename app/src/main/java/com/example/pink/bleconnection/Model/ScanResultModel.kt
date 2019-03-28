package com.example.pink.bleconnection.Model

import com.google.android.gms.maps.model.LatLng

class ScanResultModel(position : LatLng,power:Int){
    private var beaconPosition : LatLng = LatLng(0.0,0.0) //default
    private var txPower : Int = 0
    private var signal : ArrayList<Int> = arrayListOf()
    init {
        this.beaconPosition = position
        this.txPower = power
    }
    /*fun Detail(position : LatLng,power:Int){
        this.beaconPosition = position
        this.txPower = power
    }*/
    fun addSignal(signal:Int){
        this.signal.add(signal)
    }
    fun clearSignal(){
        signal.clear()
    }
    fun getAverageSignal():Double{
        /*if (signal.size!=0)return signal.average()
        else return -1.0*/
        var average : Double = 0.0
        val size : Int = signal.size
        if (size != 0)
        {
        for (i in signal){
            average += i
        }
            average /= size
        }
        return average
    }
    fun getArraySize():Int{
        return this.signal.size
    }
    fun getPosition():LatLng{
        return this.beaconPosition
    }
    fun getPower():Int{
        return this.txPower
    }
    fun setPosition(position: LatLng){
        this.beaconPosition = position
    }
    fun setPower(power: Int){
        this.txPower = power
    }
}