package com.getrentbd.timer.entity

import android.provider.BaseColumns

object TimerEntity : BaseColumns {
    var TABLE_NAME = "timerEntity"
    var START_TIME = "startTime"
    var STOP_TIME = "stopTime"
    var TABLE_UPGRADE = "DROP IF EXISTS " + TABLE_NAME
}