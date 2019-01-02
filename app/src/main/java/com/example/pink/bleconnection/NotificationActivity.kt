package com.example.pink.bleconnection

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_notification.*
import android.os.Build
import android.widget.Toast
import android.app.NotificationChannel

class NotificationActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        noti1.setOnClickListener {
            showNotification("Testing1","Complete")
        }
        noti2.setOnClickListener {
            showNotification("Testing2","New Carry")
        }
    }

    fun showNotification(textTitle : String,textContent : String){
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(this, "1");
        if (notificationManager == null) {
            println("Error?")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            var mChannel = notificationManager.getNotificationChannel("1")
            if (mChannel == null) {
                mChannel = NotificationChannel("1", title, importance)
                mChannel.enableVibration(true)
                mChannel.setVibrationPattern(longArrayOf(500, 1000, 500))
                notificationManager.createNotificationChannel(mChannel)
            }
            builder.setContentTitle(textTitle)
                    .setSmallIcon(R.drawable.ic_android_black_24dp)
                    .setContentText(textContent)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setVibrate(longArrayOf(500, 1000, 500))
        }else{
            builder.setContentTitle(textTitle)
                    .setSmallIcon(R.drawable.ic_android_black_24dp)
                    .setContentText(textContent)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setVibrate(longArrayOf(500, 1000, 500))
                    .priority = Notification.PRIORITY_HIGH
        }
        val notification = builder.build()
        notificationManager.notify(1, notification)
    }
    fun toast(text : String){
        Toast.makeText(this,text, Toast.LENGTH_SHORT).show()
    }
}