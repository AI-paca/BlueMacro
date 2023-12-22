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
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
import android.support.v4.media.session.MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class BluetoothEventService : Service() {

    private lateinit var bluetoothEvent: BluetoothEvent
    private val actionButtonPress = "com.example.bluemacro.ACTION_BUTTON_PRESS"
    private var mediaSession: MediaSessionCompat? = null
    private val FLAG_HANDLES_TRANSPORT_CONTROLS = 200
    private val FLAG_HANDLES_MEDIA_BUTTONS = 0

    // Добавлены переменные для отслеживания состояния
    private var isMediaSessionActive = false
    private var isMediaSessionAddressedApp = false

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("BluetoothEventService", "onStartCommand called")
        if (Intent.ACTION_MEDIA_BUTTON == intent.action) {
            Log.d("BluetoothEventService", "Received ACTION_MEDIA_BUTTON")
            onButtonPressed(intent)
        }

        // Проверяем, существует ли MediaSession
        mediaSession?.let {
            // MediaSession существует, используем ее
            it.setCallback(object : MediaSessionCompat.Callback() {
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

            it.setFlags( FLAG_HANDLES_MEDIA_BUTTONS)
            Log.d(
                "BluetoothEventService",
                "MediaSession isActive: $it.isActive, isAddressedApp: $isMediaSessionAddressedApp"
            )

            // Устанавливаем свою MediaSession в качестве активной, если она не является адресованной другим приложением
            if (!isMediaSessionAddressedApp) {
                it.isActive = true
            }
        } ?: {
            // MediaSession не существует, создаем ее
            createMediaSession()
        }


        // Устанавливаем флаги, позволяющие другим приложениям перехватывать события медиа-кнопок и управлять воспроизведением
        mediaSession?.setFlags(  FLAG_HANDLES_MEDIA_BUTTONS )

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

    private fun createMediaSession() {
        if (mediaSession == null) {
            mediaSession = MediaSessionCompat(this, "BluetoothEventService").apply {
                setCallback(object : MediaSessionCompat.Callback() {
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
                setFlags(FLAG_HANDLES_MEDIA_BUTTONS)
                setMediaButtonReceiver(null)
                isActive = true
            }
        }
    }


    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("channel_01", "My Channel", NotificationManager.IMPORTANCE_DEFAULT)

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)

            val notification = NotificationCompat.Builder(this, "channel_01").build()
            startForeground(1, notification)
        }
        Log.d("BluetoothEventService", "onCreate called")

        // Создание MediaSession
        mediaSession = MediaSessionCompat(this, "BluetoothEventService").apply {
            setCallback(object : MediaSessionCompat.Callback() {
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
            setFlags(FLAG_HANDLES_MEDIA_BUTTONS)
            setMediaButtonReceiver(null)
            isActive = true
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mediaSession?.release()
        mediaSession = null
    }
}



interface BluetoothEvent {
    fun onButtonPress()
}