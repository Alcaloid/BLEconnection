package com.example.pink.bleconnection

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.pink.bleconnection.BLEScanner.BluetoothReciveRSSIActivity
import com.example.pink.bleconnection.Map.Maps2Activity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var intent : Intent
        button_main.setOnClickListener {
            intent = Intent(this, TestPermission::class.java)
            startActivity(intent)
        }
        BLEbutton.setOnClickListener {
            intent = Intent(this, BluetoothReciveRSSIActivity::class.java)
            startActivity(intent)
        }
        mapbutton.setOnClickListener {
            intent = Intent(this, SearchSystemActivity::class.java)
            startActivity(intent)
        }
        googlemap_buttom.setOnClickListener {
            intent = Intent(this, Maps2Activity::class.java)
            startActivity(intent)
        }
        fragment_button.setOnClickListener {
            intent = Intent(this, PlateActivity::class.java)
            startActivity(intent)
        }
    }
}
