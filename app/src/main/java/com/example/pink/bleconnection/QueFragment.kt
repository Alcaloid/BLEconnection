package com.example.pink.bleconnection

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.PointF
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dlazaro66.qrcodereaderview.QRCodeReaderView
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.fragment_que.*

class QueFragment : Fragment(){
    private var qrCodeReaderView: QRCodeReaderView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_que, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        qrCodeReaderView = view.findViewById(R.id.qrdecoderview)
        qrCodeReaderView?.setOnQRCodeReadListener { text, points -> toast("Text :"+text) }
        qrCodeReaderView?.setQRDecodingEnabled(true)
        button_que_camera.setOnClickListener {
            Dexter.withActivity(activity)
                    .withPermission(Manifest.permission.CAMERA)
                    .withListener(object : PermissionListener{
                        override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                            layout_scan_qr.visibility = View.GONE
                            relative_que_camera_open.visibility = View.VISIBLE
                        }
                        override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {}
                        override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                            toast("This canera use to get queuing")
                        }
                    }).check()
        }
        button_camera_closer.setOnClickListener {
            layout_scan_qr.visibility = View.VISIBLE
            relative_que_camera_open.visibility = View.GONE
        }
    }

    fun showNotification(textTitle : String,textContent : String,context: Context){
        val notificationManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context, "1")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            var mChannel = notificationManager.getNotificationChannel("1")
            if (mChannel == null) {
                mChannel = NotificationChannel("1",textTitle, importance)
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
        Toast.makeText(context,text, Toast.LENGTH_SHORT).show()
    }
}