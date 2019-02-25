package com.example.pink.bleconnection.Model

import android.content.Context
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import kotlin.math.min

class CalculatorFunction(){
    public fun calDistanceFromRSSI(rssi: Double,txPower:Int):Double{
        val n: Int = 1
        val distance : Double = Math.pow(10.0,((txPower-rssi)/(10.0*n)))
        return distance
    }
    public fun calAverage(dataArray: ArrayList<Int>):Int{
        var averageNumber : Int = -100000
        if (!dataArray.isEmpty()){
            averageNumber = 0
            for (i in 0..dataArray.size-1){
                averageNumber = averageNumber + dataArray[i]
            }
            averageNumber = averageNumber/dataArray.size
        }
        return averageNumber
    }
    public fun calLocation(x1:Double,x2:Double,x3:Double,
                           y1:Double,y2:Double,y3:Double,
                           distance1:Double,distance2:Double,distance3:Double) : LatLng{
        val valueA = (-2*x1) + (2*x2)
        val valueB = (-2*y1) + (2*y2)
        val valueC = (distance1*distance1) - (distance2*distance2) - (x1*x1) + (x2*x2) - (y1*y1) + (y2*y2)
        val valueD = (-2*x2) + (2*x3)
        val valueE = (-2*y2) + (2*y3)
        val valueF = (distance2*distance2) - (distance3*distance3) - (x2*x2) + (x3*x3) - (y2*y2) + (y3*y3)
        val finalX = (((valueC*valueE) - (valueF*valueB))/((valueE*valueA)-(valueB*valueD)))
        val finalY = (((valueC*valueD)-(valueA*valueF))/((valueB*valueD)-(valueA*valueE)))
        val latLng = LatLng(finalX,finalY)
        return latLng
    }
    fun calDijistra(nodeAndpath : ArrayList<PointOfLine>,myLocal:LatLng,targer:LatLng):ArrayList<Int>{
        var minPath : ArrayList<Int> = arrayListOf()
        
        return minPath
    }
    fun toast(text : String,context: Context){
        Toast.makeText(context,text, Toast.LENGTH_SHORT).show()
    }
}