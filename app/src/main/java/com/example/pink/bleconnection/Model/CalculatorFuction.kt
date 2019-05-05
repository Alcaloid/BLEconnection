package com.example.pink.bleconnection.Model

import android.content.Context
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import kotlin.math.abs
import kotlin.math.min

class CalculatorFunction(){
    public fun calDistanceFromRSSI(rssi: Double,txPower:Int):Double{
        val n: Int = 1
        val distance : Double = Math.pow(10.0,((txPower-rssi)/(10.0*n)))
        return distance
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
    fun trilateration(circles:ArrayList<ScanResultModel>):LatLng{
//        println("before")
//        circles.map {println(it.getUUID()+" "+it.getBeaconPosition()+" "+it.getAvgSignal()) }
        define_triangle(circles)
        println("after")
        circles.map {println(it.getUUID()+" "+it.getBeaconPosition()+" "+it.getAvgSignal()) }
        var c1_center = circles[0].getBeaconPosition()
        var c2_center = circles[1].getBeaconPosition()
        var c3_center = circles[2].getBeaconPosition()
        var r1 = circles[0].getDistance()
        var r2 = circles[1].getDistance()
        var r3 = circles[2].getDistance()
        var U = 0.0
        if (c1_center.longitude != c2_center.longitude){
            U = abs(c1_center.longitude - c2_center.longitude)
        }else{
            U = abs(c1_center.longitude - c3_center.longitude)
        }
        println("U = "+U)
//        var U = Math.sqrt(Math.pow(c1_center.longitude - c2_center.longitude,2.0) + Math.pow(c1_center.latitude - c2_center.latitude,2.0))
        var lenE = ( ( Math.pow(r1,2.0) - Math.pow(r2,2.0) + Math.pow(U,2.0) ) / ( 2*U ) )
        var V_N = c3_center.latitude - c1_center.latitude
        var N : Double = c1_center.latitude
        var E : Double = c1_center.longitude
        if(V_N != 0.0){
            var V_E = c3_center.longitude - c1_center.longitude
            var V_P = Math.pow(V_N,2.0) + Math.pow(V_E,2.0)
            var lenN = ( Math.pow(r1,2.0) - Math.pow(r3,2.0) + V_P - 2*V_E*lenE ) / (2*V_N)
            N += lenN
        }
        E += lenE

        return LatLng(N,E)
    }
    fun define_triangle(circles:ArrayList<ScanResultModel>) : ArrayList<ScanResultModel>{
//        println("in define_triangle")
//        circles.map { print(it.getUUID()) }
        var loMin:Double = 10000.0
        var loMax:Double = -10000.0
        var loMin_Duc : Boolean = false
        var loMax_Duc : Boolean = false
        var temp:ArrayList<ScanResultModel> = arrayListOf()
        for (c in circles){
            temp.add(c)
            if(c.getBeaconPosition().longitude <= loMin )
                loMin = c.getBeaconPosition().longitude
            if(c.getBeaconPosition().longitude >= loMax)
                loMax = c.getBeaconPosition().longitude
        }
        var checkDucMin : ArrayList<Double> = arrayListOf()
        var checkDucMax : ArrayList<Double> = arrayListOf()
        for (t in temp){
            t.getBeaconPosition().longitude - loMin
        }
        println("loMin = "+loMin)
        println("loMax = "+loMax)
        println("loMin_Duc = "+loMin_Duc)
        println("loMax_Duc = "+loMax_Duc)

        if(loMin_Duc == true){
            var tRemove:ScanResultModel? = null
            println("In loMin_Duc")
            for (t in temp){
                if(t.getBeaconPosition().longitude == loMax){
                    circles[1] = t
                    tRemove = t
                }
            }
            temp.remove(tRemove)
            if(abs(temp[0].getBeaconPosition().latitude - circles[1].getBeaconPosition().latitude) <= abs(temp[1].getBeaconPosition().latitude - circles[1].getBeaconPosition().latitude)){
                circles[0] = temp[0]
                circles[2] = temp[1]
            }
            else{
                circles[0] = temp[1]
                circles[2] = temp[0]
            }
        }
        else if(loMax_Duc == true){
            var tRemove:ScanResultModel? = null
            println("In loMax_Duc")
            for (t in temp){
                if(t.getBeaconPosition().longitude == loMin){
                    circles[0] = t
                    tRemove = t
                }
            }
            temp.remove(tRemove)
            if(abs(temp[0].getBeaconPosition().latitude - circles[1].getBeaconPosition().latitude) <= abs(temp[1].getBeaconPosition().latitude - circles[1].getBeaconPosition().latitude)){
                circles[1] = temp[0]
                circles[2] = temp[1]
            }
            else{
                circles[2] = temp[0]
                circles[1] = temp[1]
            }
        }

        else{
//            println("----------------------------")
            for (t in temp) {
                var compare : Double = t.getBeaconPosition().longitude
                if (compare == loMin){
                    circles[0] = t
                }

                else if(compare == loMax){
                    circles[1] = t
                }
                else{
                    circles[2] = t
                }
            }
        }
        temp.clear()
        return circles
    }
    fun expoAverage(last:LatLng,current:LatLng) : LatLng{
        if(last == LatLng(-1000.0,-1000.0))
            return current
        println("inside")
        var alpha = 0.3
        var result : LatLng = LatLng(alpha*current.latitude + (1-alpha)*last.latitude, alpha*current.longitude + (1-alpha)*last.longitude)
        return result
    }
    fun calDijistra(nodeAndpath : ArrayList<PointOfLine>,myLocal:LatLng,targer:LatLng):ArrayList<Int>{
        var minPath : ArrayList<Int> = arrayListOf()
        
        return minPath
    }
    fun toast(text : String,context: Context){
        Toast.makeText(context,text, Toast.LENGTH_SHORT).show()
    }
}