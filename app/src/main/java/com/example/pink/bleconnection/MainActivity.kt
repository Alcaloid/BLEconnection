package com.example.pink.bleconnection

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.pink.bleconnection.BLEScanner.BluetoothReciveRSSIActivity
import com.example.pink.bleconnection.Map.MapFragment
import com.example.pink.bleconnection.Map.Maps2Activity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var intent : Intent
        val fragment = MapFragment()
        val fragmentTan = supportFragmentManager.beginTransaction()
        //button_main.text = "Testing"
        button_main.setOnClickListener {
            intent = Intent(this, TestPermission::class.java)
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
            intent = Intent(this, SearchSystemActivity::class.java)
            startActivity(intent)
        }
        googlemap_buttom.setOnClickListener {
            intent = Intent(this, Maps2Activity::class.java)
            startActivity(intent)
        }
        fragment_button.setOnClickListener {
            button_list.visibility = View.GONE
            fragmentTan.add(R.id.contaner,fragment)
            fragmentTan.commit()
        }
    }
}
