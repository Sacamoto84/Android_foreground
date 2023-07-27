package com.example.foreground

import android.R
import android.R.id.input
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.foreground.ui.theme.ForegroundTheme


class ForegroundService() : Service() {

    override fun onCreate() {
        super.onCreate()
        println("MyService onCreate")
    }

    private fun runAsForeground() {

        createNotificationChannel()

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            0
        )
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setContentText("input")
            .setSmallIcon(R.drawable.btn_minus)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)

        //do heavy work on a background thread
        //stopSelf();
        //do heavy work on a background thread
        //stopSelf();



    }


    private val CHANNEL_ID = "MyForegroundServiceChannel"


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("!!! onStartCommand !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
        runAsForeground()
        return START_STICKY

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }


    private fun sendBroadcastMessage() {
        val broadcastIntent = Intent()
        broadcastIntent.action = "com.example.MY_ACTION"
        broadcastIntent.putExtra("message", "my_value")
        sendBroadcast(broadcastIntent)
    }








}


class MainActivity : ComponentActivity() {

    fun startService() {
        val serviceIntent = Intent(this, ForegroundService::class.java)
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android")
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    fun stopService() {
        val serviceIntent = Intent(this, ForegroundService::class.java)
        stopService(serviceIntent)
    }


    private val myBroadcastReceiver = MyBroadcastReceiver()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//
//        val intentFilter = IntentFilter()
//        intentFilter.addAction("com.example.MY_ACTION")
//        registerReceiver(myBroadcastReceiver, intentFilter)

        //val serviceIntent = Intent(this, MyForegroundService::class.java)
        ///serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android")
        //ContextCompat.startForegroundService(this, serviceIntent)

        startService()

        setContent {
            ForegroundTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        stopService()
    }

    inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val message = intent?.getStringExtra("message")
            // Обработка полученного сообщения
            println(message)

        }
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

