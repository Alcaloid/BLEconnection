package com.example.pink.bleconnection

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.PointF
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
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
import java.text.DecimalFormat
import java.util.*
import kotlin.concurrent.timer

class QueFragment : Fragment(){
    lateinit var dataBase: FirebaseFirestore
    lateinit var callQueue : DocumentReference
    lateinit var waitQueue : CollectionReference
    lateinit var timeToWait : CollectionReference
    private var isQrQueue : Boolean = false
    private var onQue : Boolean = false
    private var myQueue : Int? = null
    private var stringOfText : ArrayList<String> = arrayListOf()
    private var buff : String = ""
    private var stateQueueHashMap : HashMap<String,Any?> = HashMap()
    var timer: CountDownTimer? = null
    var currentQueue : Int? = null
    var waitingNumber : Int? = null
    var averageTimeToWait : Long = 0
    var waitingTime : Long? = null
    var startTime : Long? = null //set when user get queue
    var endTime : Long? = null //set when current queue = myQueue
    val dateData = Calendar.getInstance()
    var dateValue : Date? = null
    val dummyTimeData = Date(119,4,7)

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
        timeToWait = dataBase.collection("TimeZone")
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
            //Drop user queue
            setStartPage()
            changeStateHold()
            setStartPage()
        }
    }
    fun setStartPage(){
        onQue = false
        timer!!.cancel()
        text_currentque.text = "-"
        text_que_waiting_number.text = "-"
        text_que_waiting_time.text = "00:00:00"
        layout_scan_qr.visibility = View.VISIBLE
        layout_getqueue.visibility = View.GONE
    }
    fun qrCodeScanner(context: Context){
        qrdecoderview.setQRDecodingEnabled(true)
        //set funtion after scan
        qrdecoderview.setOnQRCodeReadListener{text, points ->
            println("Data:"+text)
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
            //stringOfText.forEach {i-> println("Data:"+i) }
            isQrQueue = checkQrcode(stringOfText[0])
            if (isQrQueue && !onQue){
                //is qrscan is our Queue code
                //and user doesn't get que
                waitQueue.document(stringOfText[2]).get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                //see this queue is not one get
                                val isQue:Boolean = document.get("State") as Boolean
                                if (!isQue){
                                    onQue = true
                                    myQueue = stringOfText[2].toInt()
                                    stateQueueHashMap["QueueNumber"] = myQueue
                                    stateQueueHashMap["State"] = true
                                    stateQueueHashMap["Hold"] = true
                                    waitQueue.document(stringOfText[2])
                                            .set(stateQueueHashMap)
                                    layout_getqueue.visibility = View.VISIBLE
                                    relative_que_camera_open.visibility = View.GONE
                                    text_myqueue.text = myQueue.toString()
                                    qrdecoderview.stopCamera()
                                    dateValue = dateData.time
                                    startTime = dateValue!!.time
                                    callQueue.get().addOnSuccessListener { doc ->
                                        if (doc != null) {
                                            currentQueue = doc.getDouble("QueueNumber")!!.toInt()
                                            waitingNumber = myQueue!! - currentQueue!!
                                            text_currentque.text = currentQueue.toString()
                                            text_que_waiting_number.text = waitingNumber.toString()
                                        }else{
                                            toast("Can't get CallQueue Data")
                                        }
                                    }
                                    //query dummy time data by zone > 6 May 2019 , < 8 May 2019
                                    timeToWait  .whereGreaterThan("Date",Date(119,4,6))
                                                .whereLessThan("Date",Date(119,4,8))
                                                .get()
                                                .addOnSuccessListener {
                                                    for (dataTime in it){
                                                        averageTimeToWait += dataTime.data.get("WaitingTime") as Long
                                                    }
                                                    averageTimeToWait = averageTimeToWait.div(it.size())
                                                    waitingTime = waitingNumber!!*averageTimeToWait
                                                    timer = object: CountDownTimer(waitingTime!!.toLong(),1000) {
                                                        override fun onTick(millisUntilFinished: Long) {
                                                            val f = DecimalFormat("00")
                                                            val hour = millisUntilFinished / 3600000 % 24
                                                            val min = millisUntilFinished / 60000 % 60
                                                            val sec = millisUntilFinished / 1000 % 60
                                                            if (onQue){
                                                                text_que_waiting_time.setText(f.format(hour) + ":" + f.format(min) + ":" + f.format(sec))
                                                            }else{
                                                                text_que_waiting_time.text = "--:--:--"
                                                                timer!!.cancel()
                                                            }
                                                        }
                                                        override fun onFinish() {
                                                            text_que_waiting_time.setText("00:00:00")
                                                        }
                                                    }.start()
                                    }
                                    showNotification("Notification", "Get queue",context)
                                }else{
                                    toast("This queue is someone get")
                                }
                            } else {
                                toast("Queue Error")
                            }
                        }
                        .addOnFailureListener { exception ->
                            toast("Device can't get queue with:"+exception)
                        }
            }
        }
    }
    fun checkQueueUpdate(context: Context){
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
                    //waitingTime = waitingNumber!!*10
                    text_currentque.text = currentQueue.toString()
                    text_que_waiting_number.text = waitingNumber.toString()
                    //text_que_waiting_time.text = waitingTime.toString()
                    //if current queue near myqueue it's noti user
                    if ((snapshot.getDouble("QueueNumber")!!.toInt() >= myQueue!!-5)&&(snapshot.getDouble("QueueNumber")!!.toInt() < myQueue!!)) {
                        if(snapshot.getDouble("QueueNumber")!!.toInt() == myQueue!!-1){
                            showNotification("Alert", "Next queue is your queue",context)
                        }else{
                            showNotification("Alert", "Close to your queue",context)
                        }
                    }
                    if (snapshot.getDouble("QueueNumber")!!.toInt() == myQueue) {
                        showNotification("Test", "It's Your Que",context)
                        setDataOnMyQueue()
                        setStartPage()
                    }
                }
            } else {
                toast("Current data:null")
            }
        })
    }
    fun changeStateHold(){
        stateQueueHashMap["Hold"] = false
        waitQueue.document(myQueue.toString())
                .set(stateQueueHashMap)
    }
    fun setDataOnMyQueue(){
        //set data Hold on WaitingQueue collection to false
        changeStateHold()
        //sent data to TimeStamp
        endTime = dateValue!!.time
        val result = endTime!! - startTime!!
        stateQueueHashMap["Date"] = dateValue
        stateQueueHashMap["WaitingTime"] = result
        timeToWait.document().set(stateQueueHashMap)
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