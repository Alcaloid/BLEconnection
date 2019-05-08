package com.example.pink.bleconnection

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_test_page.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*



class TestPageActivity : AppCompatActivity() {
    lateinit var dataBase: FirebaseFirestore
    lateinit var timeToWait : CollectionReference
    private var stateQueueHashMap : HashMap<String,Any?> = HashMap()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_page)
        dataBase = FirebaseFirestore.getInstance()
        timeToWait = dataBase.collection("TimeZone")
        button_tester.setOnClickListener {
            val currentTime = Date(119,4,8,16,59,0)
            val endTime = Date(119,4,8,17,0,0)
            val result = endTime.time - currentTime.time
            result_text.text = result.toString()
            stateQueueHashMap["Date"] = Calendar.getInstance().time
            stateQueueHashMap["WaitingTime"] = result
            timeToWait.document().set(stateQueueHashMap)
        }
    }
}
