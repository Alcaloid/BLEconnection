package com.example.pink.bleconnection

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.pink.bleconnection.Model.MyFmPagerAdapter
import kotlinx.android.synthetic.main.activity_plate.*

class PlateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plate)
        val myFmPagerAdapter : MyFmPagerAdapter = MyFmPagerAdapter(supportFragmentManager)
        page_viewer.adapter = myFmPagerAdapter
        tabs_plate.setupWithViewPager(page_viewer)

    }
}
