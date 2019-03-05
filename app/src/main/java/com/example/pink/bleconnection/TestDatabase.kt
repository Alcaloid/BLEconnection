package com.example.pink.bleconnection

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_search_system.*


class TestDatabase : AppCompatActivity() {
    //onMobile
    lateinit var dataBase: FirebaseFirestore
    lateinit var callQueue : DocumentReference
    lateinit var waitQueue : CollectionReference
    private var onQue : Boolean = false
    private var userQuery : Int? = null
    private var queWaiting : Int = 0
    private var waitingQueHashMap : HashMap<String,Any> = HashMap()

    //onWeb
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_system)

        init()
        checkQueueUpdate()
        moblieOperation()
        webOperation()
    }
    fun init(){
        dataBase = FirebaseFirestore.getInstance()
        callQueue = dataBase.collection("CallQueue").document("Queue")
        waitQueue = dataBase.collection("WaitingQueue")
    }
    fun checkQueueUpdate(){
        //checkUpdate
        callQueue.addSnapshotListener(EventListener<DocumentSnapshot>{snapshot,e->
            if (e != null){
                toast("Listen failed " + e)
                return@EventListener
            }
            if (snapshot != null && snapshot.exists()) {
                //toast("Current data:"+snapshot.data)
                if (snapshot.getDouble("QueueNumber")!!.toInt()<0){
                    text_show_currentque.text = "none"
                }else{
                    text_show_currentque.text = snapshot.getDouble("QueueNumber")!!.toInt().toString()
                }
                if (onQue){
                    if (snapshot.getDouble("QueueNumber")!!.toInt() == userQuery){
                        showNotification("Test","It's Your Que")
                        button_getque.text = "Get queue"
                        onQue = !onQue
                    }
                }else{
                    userQuery = null
                }
            } else {
                toast("Current data:null")
            }
        })
    }
    fun moblieOperation(){
        button_getque.setOnClickListener {
            if (onQue){
               //cancle que
                button_getque.text = "Get que"
                waitingQueHashMap["State"] = "Cancle"
                waitQueue.document(queWaiting.toString())
                        .set(waitingQueHashMap)
                text_show_yourque.text = "none"
            }else{
                //want que
                button_getque.text = "Cancle que"
                waitQueue.orderBy("QueueNumber",Query.Direction.DESCENDING)
                        .limit(1)
                        .get()
                        .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot>{
                            override fun onComplete(task: Task<QuerySnapshot>) {
                                if (task.isSuccessful){
                                    for (document : DocumentSnapshot in task.result!!){
                                        queWaiting = document.id.toInt()
                                    }
                                    queWaiting += 1
                                    userQuery = queWaiting
                                    waitingQueHashMap["QueueNumber"] = queWaiting
                                    waitingQueHashMap["State"] = "Waiting"
                                    waitQueue.document(queWaiting.toString())
                                            .set(waitingQueHashMap)
                                    text_show_yourque.text = userQuery.toString()
                                }
                            }
                        })
            }
            onQue = !onQue
        }
    }
    fun webOperation(){
        var updaterQueue : Int = 0
        val nextQueHashMap : HashMap<String,Int> = HashMap()
        var valueInQue : Int = 0
        waitQueue.orderBy("QueueNumber",Query.Direction.DESCENDING)
                .limit(1)
                .addSnapshotListener(EventListener<QuerySnapshot>{snapshot,e->
                    if (e != null){
                        toast("Listen failed " + e)
                        return@EventListener
                    }
                    if (snapshot != null) {
                        valueInQue = snapshot.documents[0].getDouble("QueueNumber")!!.toInt()
                        println("Value is "+valueInQue)
                    }
        })
        button_next_que.setOnClickListener {
            callQueue.get()
                    .addOnSuccessListener {documentSnapshot ->
                        if (documentSnapshot!=null){
                            updaterQueue = documentSnapshot.getDouble("QueueNumber")!!.toInt()
                            if (updaterQueue!=valueInQue){
                                nextQueHashMap["QueueNumber"] = updaterQueue + 1
                                callQueue.set(nextQueHashMap)
                            }else{
                                toast("Not enogh Queue")
                            }
                        }else{
                            toast("Can't Update")
                        }
                    }
        }
    }
    fun showNotification(textTitle : String,textContent : String){
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(this, "1")
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
        Toast.makeText(this,text, Toast.LENGTH_SHORT).show()
    }
}

