package com.example.pink.bleconnection

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_search_system.*
import java.util.*

class SearchSystemActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_system)
        val array : Array<Int> = arrayOf(1,2)
        var rand : Int
        button_random.setOnClickListener {
            rand  = Random().nextInt(array.size)
            text_rand.text = rand.toString()
        }
    }

    fun toast(text : String){
        Toast.makeText(this,text, Toast.LENGTH_SHORT).show()
    }
}

