package com.example.bluemacro.bluetooth.events

import android.content.Intent
import android.util.Log
import android.view.KeyEvent

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.media.session.MediaSession
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class BluetoothEventService : Service() {

    private lateinit var bluetoothEvent: BluetoothEvent
    private val actionButtonPress = "com.example.bluemacro.ACTION_BUTTON_PRESS"
    private lateinit var mediaSession: MediaSession

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("BluetoothEventService", "onStartCommand called")
        if (Intent.ACTION_MEDIA_BUTTON == intent.action) {
            Log.d("BluetoothEventService", "Received ACTION_MEDIA_BUTTON")
            onButtonPressed(intent)
        }
        return START_STICKY
    }

    private fun onButtonPressed(intent: Intent) {
        Log.d("BluetoothEventService", "onButtonPressed called")
        val event = intent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
        event?.let {
            if (KeyEvent.ACTION_DOWN == it.action) {
                Log.d("BluetoothEventService", "Button pressed")
                // Отправьте широковещательное намерение, чтобы уведомить о нажатии кнопки
                val buttonPressIntent = Intent(actionButtonPress)
                LocalBroadcastManager.getInstance(this).sendBroadcast(buttonPressIntent)
            }
        }
    }


    override fun onCreate() {
        super.onCreate()
        Log.d("BluetoothEventService", "onCreate called")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("channel_01", "My Channel", NotificationManager.IMPORTANCE_DEFAULT)

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)

            val notification = NotificationCompat.Builder(this, "channel_01").build()
            startForeground(1, notification)
        }

        // Создание MediaSession
        mediaSession = MediaSession(this, "BluetoothEventService").apply {
            setCallback(object : MediaSession.Callback() {
                override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
                    val ke: KeyEvent? = mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT)
                    if (ke != null && ke.action == KeyEvent.ACTION_DOWN) {
                        Log.d("BluetoothEventService", "Button pressed")
                        onButtonPressed(mediaButtonIntent)
                        return true
                    }
                    return super.onMediaButtonEvent(mediaButtonIntent)
                }
            })
            setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS)
            setMediaButtonReceiver(null)
            isActive = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.release()
    }
}



interface BluetoothEvent {
    fun onButtonPress()
}

