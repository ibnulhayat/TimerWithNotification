package com.getrentbd.timer.entity

object AppConstants {
    const val ACTION_STOP = "Stop"
    const val ACTION_PAUSE = "Pause"
    const val ACTION_RESUME = "Resume"
    const val ACTION_START = "Start"
    var secondsRemaining: Long = 0
    var nowSeconds: Long = 0
    var state = ""
    var runingTime: String? = null
    var SHARE = "com.getrent.timer"
    var TOTAL = "total"
    var RUNNING = "running"
    var WALCK = "walkup"
}