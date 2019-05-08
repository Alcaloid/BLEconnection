package com.example.pink.bleconnection

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        var intent: Intent
        button_mainplate.setOnClickListener {
            intent = Intent(this,PlateActivity::class.java)
            startActivity(intent)
        }
        button_test.setOnClickListener {
            intent = Intent(this,TestPageActivity::class.java)
            startActivity(intent)
        }
    }
}
