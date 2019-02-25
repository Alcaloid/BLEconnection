package com.example.pink.bleconnection.Model

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.pink.bleconnection.Map.IndoorMapFragment
import com.example.pink.bleconnection.QueFragment

class MyFmPagerAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm){

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> QueFragment()
            1 -> IndoorMapFragment()
            else -> QueFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position){
            0 -> "Que Page"
            1 -> "Map Page"
            else -> {
                return "Query Page"
            }
        }
    }

}