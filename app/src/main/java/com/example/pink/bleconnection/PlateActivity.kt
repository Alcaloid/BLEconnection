package com.example.pink.bleconnection

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.pink.bleconnection.Map.MapFragment
import kotlinx.android.synthetic.main.activity_plate.*

class PlateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plate)
        var onMap : Boolean = false
        val fragmentTan = supportFragmentManager.beginTransaction()
        fragmentTan.add(R.id.contraner,QueFragment())
        fragmentTan.commit()

        button_plate_to_que.setOnClickListener {
            if (onMap){
                onMap = !onMap
                fragmentTan.add(R.id.contraner,QueFragment())
                fragmentTan.commit()
            }
        }
        button_plate_to_map.setOnClickListener {
            if (!onMap){
                onMap = !onMap
                fragmentTan.add(R.id.contraner,MapFragment())
                fragmentTan.commit()
            }
        }
    }
}
