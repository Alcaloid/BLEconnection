package com.example.pink.bleconnection.Model

import com.google.android.gms.maps.model.LatLng

class BeaconDetail{
    private var beaconPosition : LatLng = LatLng(0.0,0.0) //default
    private var txPower : Int = 0
    private var signal : ArrayList<Int> = arrayListOf()

    fun Detail(position : LatLng,power:Int){
        this.beaconPosition = position
        this.txPower = power
    }
    fun addSignal(signal:Int){
        this.signal.add(signal)
    }
    fun clearSignal(){
        signal.clear()
    }
    fun getAverageSignal():Double{
        var average : Double = 0.0
        val size : Int = signal.size
        for (i in signal){
            average += i
        }
        if (size != 0)
        {
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