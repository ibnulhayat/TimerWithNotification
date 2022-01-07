package com.getrentbd.timer.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.getrentbd.timer.R
import com.getrentbd.timer.entity.AppConstants
import com.getrentbd.timer.recever.NotificationActionReceiver

class NotificationUtil {
    private val CHANNEL_ID_TIMER = "menu_timer"
    private val CHANNEL_NAME_TIMER = "Timer App Timer"
    private val TIMER_ID = 0

    fun showTimerExpired(context: Context) {
        val startIntent = Intent(context, NotificationActionReceiver::class.java)
        startIntent.action = AppConstants.ACTION_START
        val startPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(context, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val nBuilder: NotificationCompat.Builder =
            getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true)
        nBuilder.setContentTitle("Timer Expired!")
            .setContentText("Set time and Start again?")
        val nManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER)?.let {
                nManager.createNotificationChannel(
                    it
                )
            }
        }
        nManager.notify(TIMER_ID, nBuilder.build())

    }

    private fun getBasicNotificationBuilder(
        context: Context,
        channelId: String,
        playSound: Boolean
    ): NotificationCompat.Builder {
        val notificationSound: Uri =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val nBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_timer)
            .setAutoCancel(true)
            .setDefaults(0)
        if (playSound) nBuilder.setSound(notificationSound)
        return nBuilder
    }

    private fun getPendingIntentWithStack(
        context: Context,
        javaClass: Class<MainActivity>
    ): PendingIntent? {
        val resultIntent = Intent(context, javaClass)
        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addParentStack(javaClass)
        stackBuilder.addNextIntent(resultIntent)
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun showTimerRunning(context: Context) {
        val stopIntent = Intent(context, NotificationActionReceiver::class.java)
        stopIntent.action = AppConstants.ACTION_STOP
        val stopPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(context, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val pauseIntent = Intent(context, NotificationActionReceiver::class.java)
        pauseIntent.action = AppConstants.ACTION_PAUSE
        val pausePendingIntent: PendingIntent =
            PendingIntent.getBroadcast(context, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val nBuilder: NotificationCompat.Builder =
            getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true)
        nBuilder.setContentTitle("Timer is Running.")
            .setOngoing(true)
            .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)
            .addAction(R.drawable.ic_pause, "Pause", pausePendingIntent)
        val nManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID_TIMER,
                CHANNEL_NAME_TIMER,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.description = "EDMT channel"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            nManager.createNotificationChannel(notificationChannel)
        }
        nManager.notify(TIMER_ID, nBuilder.build())

    }

    fun showTimerPaused(context: Context) {
        val resumeIntent = Intent(context, NotificationActionReceiver::class.java)
        resumeIntent.action = AppConstants.ACTION_RESUME
        val resumePendingIntent: PendingIntent =
            PendingIntent.getBroadcast(context, 0, resumeIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val nBuilder: NotificationCompat.Builder =
            getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true)
        nBuilder.setContentTitle("Timer is paused.")
            .setContentText("Resume?")
            .setOngoing(true)
            .addAction(R.drawable.ic_play, "Resume", resumePendingIntent)
        val nManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER)?.let {
                nManager.createNotificationChannel(
                    it
                )
            }
        }
        nManager.notify(TIMER_ID, nBuilder.build())
    }

    fun hideTimerNotification(context: Context) {
        val nManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nManager.cancel(TIMER_ID)
    }

    private fun createChannel(channelID: String, channelName: String): NotificationChannel? {
        var notificationChannel: NotificationChannel? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.description = "EDMT channel"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
        }
        return notificationChannel
    }
}