package com.getrentbd.timer.recever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.getrentbd.timer.activity.MainActivity
import com.getrentbd.timer.activity.NotificationUtil
import com.getrentbd.timer.entity.AppConstants

class NotificationActionReceiver : BroadcastReceiver() {
    private lateinit var notificationUtil: NotificationUtil
    private lateinit var mainActivity: MainActivity
    override fun onReceive(context: Context, intent: Intent) {
        notificationUtil = NotificationUtil()
        mainActivity = MainActivity()
        val sb = StringBuilder()
        sb.append(intent.action)
        val action = sb.toString()
        AppConstants.state = action

        when {
            action.contains(AppConstants.ACTION_STOP) -> {
                mainActivity.removeAlarm(context)
                notificationUtil.hideTimerNotification(context)
            }
            action.contains(AppConstants.ACTION_PAUSE) -> {
                val secondsRemaining: Long = AppConstants.secondsRemaining
                val nowSeconds: Long = AppConstants.nowSeconds
                notificationUtil.showTimerPaused(context)
                val wakeUpTime: Long = mainActivity.setAlarm(context)
                Log.e("HHHHHHHHHHHH", wakeUpTime.toString())
            }
            action.contains(AppConstants.ACTION_RESUME) -> {
                val secondsRemaining: Long = AppConstants.secondsRemaining
                val nowSeconds: Long = AppConstants.nowSeconds
                val wakeUpTime: Long = mainActivity.setAlarm(context)
                notificationUtil.showTimerRunning(context)
            }
            action.contains(AppConstants.ACTION_START) -> {
                val wakeUpTime: Long = mainActivity.setAlarm(context)
                notificationUtil.showTimerRunning(context)
            }
        }
    }
}