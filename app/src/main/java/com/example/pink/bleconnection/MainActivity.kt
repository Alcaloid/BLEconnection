package com.example.pink.bleconnection

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.pink.bleconnection.BLEScanner.BluetoothReciveRSSIActivity
import com.example.pink.bleconnection.Map.MapActivity
import com.example.pink.bleconnection.Map.Maps2Activity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var intent : Intent
        button_main.setOnClickListener {
            intent = Intent(this, ScanBluetoothDevice::class.java)
            startActivity(intent)
        }
        BLEbutton.setOnClickListener {
            intent = Intent(this, BluetoothReciveRSSIActivity::class.java)
            startActivity(intent)
        }
        notiButton.setOnClickListener {
            intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }
        mapbutton.setOnClickListener {
            intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }
        googlemap_buttom.setOnClickListener {
            intent = Intent(this, Maps2Activity::class.java)
            startActivity(intent)
        }

//        var testingArray : Array<ArrayList<Int>> = arrayOf(
//                arrayListOf(),
//                arrayListOf(),
//                arrayListOf(),
//                arrayListOf()
//        )
//        testingArray[0].add(11)
//        testingArray[0].add(12)
//        testingArray[0].add(13)
//        testingArray[0].add(14)
//        testingArray[1].add(21)
//        testingArray[1].add(22)
//        testingArray[1].add(23)
//        testingArray[1].add(24)
//        testingArray[2].add(31)
//        testingArray[2].add(32)
//        testingArray[2].add(33)
//        testingArray[2].add(34)
//        testingArray[3].add(41)
//        testingArray[3].add(42)
//        testingArray[3].add(43)
//        testingArray[3].add(44)
//
//        for (item in testingArray){
//            for (i in 0..item.size-1){
//                println("Data:"+ i + " is "+ item[i])
//            }
//        }
    }
}
