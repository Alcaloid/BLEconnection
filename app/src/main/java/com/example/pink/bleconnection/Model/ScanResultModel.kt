package com.example.pink.bleconnection.Model

import com.google.android.gms.maps.model.LatLng

class ScanResultModel(UUID : String,position : LatLng,power:Double){
    private var UUID : String = "RLXX"
    private var beaconPosition : LatLng = LatLng(0.0,0.0) //default
    private var txPower : Double = 0.0
    private var signal : ArrayList<Double> = arrayListOf()
    private var avgSignal : Double = 0.0
    private var distance : Double = 1000.0
    init {
        this.UUID = UUID
        this.beaconPosition = position
        this.txPower = power
    }

    fun getUUID():String{
        return this.UUID
    }

    fun getBeaconPosition() : LatLng{
        return this.beaconPosition
    }

    fun addSignal(signal:Double){
        this.signal.add(signal)
    }
    fun clearSignal(){
        signal = arrayListOf()
        avgSignal = 0.0
        distance = 1000.0
    }

    fun filter(){
        val expoSignal: ArrayList<Double> = arrayListOf()
        expoSignal.addAll(signal)
        val alpha = 0.3
        for (index in 1..signal.size-1){
            expoSignal[index] = alpha*expoSignal[index] + (1-alpha)*expoSignal[index-1]
        }
        this.avgSignal = expoSignal.average()
    }

    public fun calDistanceFromRSSI(){
        val n: Int = 2
        this.distance = Math.pow(10.0,((txPower - avgSignal)/(10.0*n)))
    }

    fun getArraySize():Int{
        return this.signal.size
    }
    fun getPosition():LatLng{
        return this.beaconPosition
    }
    fun getPower():Double{
        return this.txPower
    }
    fun getAvgSignal():Double{
        return this.avgSignal
    }
    // for test delete it
    fun setAvgSignal(avgSignal:Double){
        this.avgSignal = avgSignal
    }
    fun getDistance():Double{
        return this.distance
    }
    fun setDistance(distance:Double){
        this.distance = distance
    }
    fun addSignalAll(signalList:ArrayList<Double>){
        this.signal.addAll(signalList)
    }
    fun setPosition(position: LatLng){
        this.beaconPosition = position
    }
    fun setPower(power: Double){
        this.txPower = power
    }
    fun checkEmpty():Boolean{
        if (!this.signal.isEmpty()){
            return true
        }
        return false
    }
}