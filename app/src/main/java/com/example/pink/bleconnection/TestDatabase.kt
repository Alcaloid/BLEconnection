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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_search_system.*


class TestDatabase : AppCompatActivity() {
    lateinit var dataBase: FirebaseFirestore
    lateinit var mAuth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_system)
        mAuth = FirebaseAuth.getInstance()
        dataBase = FirebaseFirestore.getInstance()
        var inQue : Boolean = false
        var foundQueue : Int = 0
        var userSetQue : HashMap<String,String> = HashMap()
        var currentQue = 0
        val queHushMap : HashMap<String,Int> = HashMap()
        //check update realtime
        val docRef = dataBase.collection("TestCallQueue").document("CallQueue")
        docRef.addSnapshotListener(EventListener<DocumentSnapshot> { snapshot, e ->
            if (e != null) {
                toast("Listen failed " + e)
                return@EventListener
            }

            if (snapshot != null && snapshot.exists()) {
                toast("Current data:"+snapshot.data)
                if (inQue){
                    println("InQue")
                    if (snapshot.getDouble("QueueNumber")!!.toInt() == foundQueue){
                        showNotification("TestJa","It's Your Que")
                    }
                }
            } else {
                toast("Current data:null")
            }
        })

        button_login.setOnClickListener {
            loginAnonymous()
        }
        button_queue.setOnClickListener {
            if (currentUser != null){
                if (!inQue){
                    println("InQueuing")
                    inQue = !inQue
                    dataBase.collection("TestJa")
                            .get()
                            .addOnCompleteListener (object : OnCompleteListener<QuerySnapshot>{
                                override fun onComplete(task: Task<QuerySnapshot>) {
                                    if (task.isSuccessful){
                                        for (document : DocumentSnapshot in task.result!!){
                                            println("ID:"+document.id.toInt())
                                            if (document.id.toInt()>foundQueue){
                                                foundQueue = document.id.toInt()
                                            }
                                        }
                                        println("Found ID:"+foundQueue)
                                        foundQueue +=1
                                        userSetQue["UUID"] = currentUser!!.uid
                                        dataBase.collection("TestJa")
                                                .document(foundQueue.toString())
                                                .set(userSetQue)
                                    }
                                }
                            })
                }
            }else{
                toast("You are not login")
            }
        }
        button_next_que.setOnClickListener {
            docRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            println("Data Document is "+document.getDouble("QueueNumber"))
                            currentQue = document.getDouble("QueueNumber")!!.toInt()
                        } else {
                            println("No data")
                        }
                    }
            currentQue +=1
            queHushMap["QueueNumber"] = currentQue
            docRef.set(queHushMap)
        }
        button_restart.setOnClickListener {
            inQue = false
            dataBase.collection("TestJa").document(foundQueue.toString())
                    .delete()
                    .addOnSuccessListener { toast("Delete data Complte") }
                    .addOnFailureListener { e -> toast("Fail to delete"+e) }
            foundQueue = 0
            queHushMap["QueueNumber"] = 0
            docRef.set(queHushMap)
        }
        /*var number = 0
        var currentQue = 0
        val queHushMap : HashMap<String,Int> = HashMap()
        val query  = dataBase.collection("TestJa")
        val docRef = dataBase.collection("TestCallQueue").document("CallQueue")
        docRef.addSnapshotListener(EventListener<DocumentSnapshot> { snapshot, e ->
            if (e != null) {
                toast("Listen failed " + e)
                return@EventListener
            }

            if (snapshot != null && snapshot.exists()) {
                toast("Current data:"+snapshot.data)
            } else {
                toast("Current data:null")
            }
        })
        button_login.setOnClickListener {
            loginAnonymous()
        }
        button_queue.setOnClickListener {
            if (currentUser != null){
                query.get().addOnCompleteListener (object : OnCompleteListener<QuerySnapshot>{
                    override fun onComplete(task: Task<QuerySnapshot>) {
                        if (task.isSuccessful){
                            for (document : DocumentSnapshot in task.result!!){
                                if (document.id.toInt()>number){
                                    number = document.id.toInt()
                                }
                            }
//                            println("number is "+ number)
                        }
                    }

                })
            }else{
                toast("You are not login")
            }
        }
        button_next_que.setOnClickListener {
            docRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            println("Data Document is "+document.getDouble("QueueNumber"))
                            currentQue = document.getDouble("QueueNumber")!!.toInt()
                        } else {
                            println("No data")
                        }
                    }
            currentQue +=1
            queHushMap["QueueNumber"] = currentQue
            docRef.set(queHushMap)
        }*/
        /*val nameData : ArrayList<String> = arrayListOf("New","Tiger","Kun","Beer","River","Boom")
        var tester : HashMap<String,String> = HashMap()
        dataBase = FirebaseFirestore.getInstance()
        var collectionReference : CollectionReference = dataBase.collection("TestJa")
        val db = dataBase.collection("TestJa").document()
        for (i in nameData){
            tester["Queue"] = i
            collectionReference.add(tester)

            //db.set(tester)
        }*/

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
    fun loginAnonymous() {
        mAuth.signInAnonymously().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                currentUser = mAuth.currentUser
                currentUser?.let { mAuth.updateCurrentUser(currentUser!!) }
            } else {
                toast("Login failed")
            }
        }
    }

    fun toast(text : String){
        Toast.makeText(this,text, Toast.LENGTH_SHORT).show()
    }
}

