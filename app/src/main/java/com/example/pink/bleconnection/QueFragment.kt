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
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.fragment_que.*

class QueFragment : Fragment(){
    lateinit var dataBase: FirebaseFirestore
    lateinit var callQueue : DocumentReference
    lateinit var waitQueue : CollectionReference
    private var isQrQueue : Boolean = false
    private var onQue : Boolean = false
    private var myQueue : Int? = null
    private var stringOfText : ArrayList<String> = arrayListOf()
    private var buff : String = ""
    private var stateQueueHashMap : HashMap<String,Boolean> = HashMap()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_que, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        qrCodeScanner(view.context)
        checkQueueUpdate(view.context)

    }
    fun init(){
        dataBase = FirebaseFirestore.getInstance()
        callQueue = dataBase.collection("CallQueue").document("Queue")
        waitQueue = dataBase.collection("WaitingQueue")
        //permissionCamera
        button_que_camera.setOnClickListener {
            Dexter.withActivity(activity)
                    .withPermission(Manifest.permission.CAMERA)
                    .withListener(object : PermissionListener{
                        override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                            layout_scan_qr.visibility = View.GONE
                            qrdecoderview.startCamera()
                            relative_que_camera_open.visibility = View.VISIBLE
                        }
                        override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {}
                        override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                            toast("This camera use to get queuing")
                        }
                    }).check()
        }
        button_camera_closer.setOnClickListener {
            layout_scan_qr.visibility = View.VISIBLE
            relative_que_camera_open.visibility = View.GONE
            qrdecoderview.stopCamera()
        }
        button_leave_queue.setOnClickListener {
            layout_scan_qr.visibility = View.VISIBLE
            layout_getqueue.visibility = View.GONE
            //Drop user queue
            onQue = false
            stateQueueHashMap["Hold"] = false
            waitQueue.document(myQueue.toString())
                    .set(stateQueueHashMap)
        }
    }
    fun qrCodeScanner(context: Context){
        qrdecoderview.setQRDecodingEnabled(true)
        //set funtion after scan
        qrdecoderview.setOnQRCodeReadListener{text, points ->
            stringOfText.clear()
            buff = ""
            text.forEachIndexed { index, c ->
                if (c != ' '){
                    buff += c
                }else{
                    stringOfText.add(buff)
                    buff = ""
                }
            }
            isQrQueue = checkQrcode(stringOfText[0])
            if (isQrQueue && !onQue){
                //is qrscan is our Queue code
                //and user doesn't get que
                if (stringOfText[1]=="QueueNumber"){
                    //this queue not someone get
                    onQue = true
                    myQueue = stringOfText[2].toInt()
                    stateQueueHashMap["State"] = true
                    stateQueueHashMap["Hold"] = true
                    waitQueue.document(stringOfText[2])
                            .set(stateQueueHashMap)
                    layout_getqueue.visibility = View.VISIBLE
                    relative_que_camera_open.visibility = View.GONE
                    text_myqueue.text = myQueue.toString()
                    qrdecoderview.stopCamera()
                    showNotification("Notification", "Get queue",context)
                }
            }
        }
    }
    fun checkQueueUpdate(context: Context){
        var currentQueue : Int? = null
        var waitingNumber : Int? = null
        var waitingTime : Int? = null
        //checkUpdate
        callQueue.addSnapshotListener(EventListener<DocumentSnapshot>{ snapshot, e->
            if (e != null){
                toast("Listen failed " + e)
                return@EventListener
            }
            if (snapshot != null && snapshot.exists()) {
                //show data when user is onque
                if (onQue) {
                    currentQueue = snapshot.getDouble("QueueNumber")!!.toInt()
                    waitingNumber = myQueue!! - currentQueue!!
                    waitingTime = waitingNumber!!*10
                    text_currentque.text = currentQueue.toString()
                    text_que_waiting_number.text = waitingNumber.toString()
                    text_que_waiting_time.text = waitingTime.toString()
                    if (snapshot.getDouble("QueueNumber")!!.toInt() == myQueue) {
                        showNotification("Test", "It's Your Que",context)
                        onQue = !onQue
                    }
                }
            } else {
                toast("Current data:null")
            }
        })
    }
    fun checkQrcode(str : String):Boolean{
        if (str == "SenoirProJectCPE2019"){
            return true
        }
        return false
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