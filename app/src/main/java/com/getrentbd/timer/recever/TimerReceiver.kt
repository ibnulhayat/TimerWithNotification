package com.getrentbd.timer.recever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.getrentbd.timer.activity.NotificationUtil

class TimerReceiver : BroadcastReceiver() {
    private lateinit var notificationUtil: NotificationUtil
    override fun onReceive(context: Context, intent: Intent) {
        notificationUtil = NotificationUtil()
        notificationUtil.showTimerExpired(context)
    }
}