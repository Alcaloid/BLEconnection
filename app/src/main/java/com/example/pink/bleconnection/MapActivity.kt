package com.example.pink.bleconnection

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_map.*
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.widget.RelativeLayout
import android.view.ViewGroup.MarginLayoutParams





class MapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

//        val marker = BitmapFactory.decodeResource(resources,
//                R.drawable.ic_add_location_black_24dp)
//        val canvas : Canvas? = null
//        canvas?.drawBitmap(marker, 40f, 40f, null)
        var positionX : Int = 0
        var positionY : Int = 0
        var changeText : String = ""
        val marginParams = MarginLayoutParams(position.getLayoutParams())
        makerbutton.setOnClickListener {
            toast("X : "+ editText_x.text + " Y : "+editText_y.text)
            changeText = editText_x.text.toString()
            if (changeText == ""){
                changeText = "0"
            }
            positionX = Integer.parseInt(changeText)
            changeText = editText_y.text.toString()
            if (changeText == ""){
                changeText = "0"
            }
            positionY = Integer.parseInt(changeText)
            println("x: "+positionX+" y:"+positionY)
            marginParams.setMargins(0 + positionX, 150 + positionY, 0, 0)
            println("total : x = "+ positionX + " total : y = "+ (150+positionY))
            val layoutParams = RelativeLayout.LayoutParams(marginParams)
            position.setLayoutParams(layoutParams)
        }

    }

    fun toast(text : String){
        Toast.makeText(this,text, Toast.LENGTH_SHORT).show()
    }

}
