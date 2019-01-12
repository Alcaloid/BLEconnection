package com.example.pink.bleconnection

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import com.google.gson.Gson


class GetJsonData : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        var gson : Gson = Gson()
        var testingData : TestingData = gson.fromJson("dataCost",TestingData::class.java)
        var dataTester : String = testingData.glossary.title
        println(dataTester)
//        val gsonBuilder = GsonBuilder().setDateFormat("dataCost")
//        var testingData = TestingData()
//        gson = gsonBuilder.create()
    }
}