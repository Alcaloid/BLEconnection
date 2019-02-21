package com.example.pink.bleconnection

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import com.example.pink.bleconnection.Map.MapFragment
import kotlinx.android.synthetic.main.activity_plate.*

class PlateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plate)
        var onMap : Boolean = false
        changeFragment(QueFragment())

        button_plate_to_que.setOnClickListener {
            if (onMap){
                onMap = !onMap
                changeFragment(QueFragment())
            }
        }
        button_plate_to_map.setOnClickListener {
            if (!onMap){
                onMap = !onMap
                changeFragment(MapFragment())
            }
        }
    }
    fun changeFragment(fragment : Fragment){
        val fragmentTan = supportFragmentManager.beginTransaction()
        fragmentTan.replace(R.id.contraner, fragment)
                .addToBackStack(null)
                .commit()
    }
}
