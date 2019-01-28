package com.example.pink.bleconnection

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import kotlinx.android.synthetic.main.activity_search_system.*

class SearchSystemActivity : AppCompatActivity() {
    val place : ArrayList<String> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_system)
        place.add("Allcama")
        place.add("Altoma")
        place.add("ABOCAMIN")
        place.add("BioHaZra")

        serachArea.queryHint = "Hint of xxx"
        serachArea.setOnQueryTextFocusChangeListener { v, hasFocus ->  }

    }
}
