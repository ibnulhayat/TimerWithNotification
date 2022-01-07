package com.getrentbd.timer.activity

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.getrentbd.timer.R
import com.getrentbd.timer.database.DBConnection
import com.getrentbd.timer.entity.AppConstants
import com.getrentbd.timer.entity.AppConstants.RUNNING
import com.getrentbd.timer.entity.AppConstants.SHARE
import com.getrentbd.timer.entity.AppConstants.TOTAL
import com.getrentbd.timer.entity.AppConstants.WALCK
import com.getrentbd.timer.entity.AppConstants.runingTime
import com.getrentbd.timer.entity.AppConstants.secondsRemaining
import com.getrentbd.timer.recever.NotificationActionReceiver
import com.getrentbd.timer.recever.TimerReceiver
import com.google.android.material.floatingactionbutton.FloatingActionButton
import me.zhanghai.android.materialprogressbar.MaterialProgressBar
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val TAG = "ACIVITYLIFECYCLE"
    private val START_TIME_IN_MILLIS: Long = 0
    private lateinit var mTextViewCountDown: TextView
    private lateinit var startPause: FloatingActionButton
    private lateinit var stop: FloatingActionButton
    private lateinit var fab_List: FloatingActionButton
    private lateinit var mCountDownTimer: CountDownTimer
    private var mTimerRunning = false
    private var mTimeLeftInMillis = START_TIME_IN_MILLIS
    private lateinit var progress_countdown: MaterialProgressBar
    private var totalProgress:Int = 0
    private var runingProgress:Int = 0
    private var state = ""
    private var nowSeconds: Long = 0
    private lateinit var notificationUtil: NotificationUtil
    private lateinit var sqliteDB: DBConnection
    private val dateFormat = SimpleDateFormat("dd MMM yyyy/hh:mm a", Locale.getDefault())
    private lateinit var submit: String
    private lateinit var preferences: SharedPreferences
    private lateinit var editor: Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationUtil = NotificationUtil()
        sqliteDB = DBConnection(this)
        Log.e(TAG, "onCreate")
    }


    fun setAlarm(context: Context): Long {
        val wakeUpTime: Long = ((nowSeconds + runingProgress) * 1000)
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationActionReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent)
        return wakeUpTime
    }

    fun removeAlarm(context: Context) {
        val intent = Intent(context, TimerReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        pauseTimer()
    }


    @SuppressLint("RestrictedApi")
    override fun onStart() {
        super.onStart()
        Log.e(TAG, "onStart")
        preferences = getSharedPreferences(SHARE, MODE_PRIVATE)
        editor = preferences.edit()
        nowSeconds = Calendar.getInstance().timeInMillis / 1000
        mTextViewCountDown = findViewById(R.id.textViewCountdown)
        progress_countdown = findViewById(R.id.progress_countdown)
        startPause = findViewById(R.id.fab_start)
        stop = findViewById(R.id.fab_stop)
        fab_List = findViewById(R.id.fab_List)
        mTextViewCountDown.setOnClickListener(View.OnClickListener {
            if (!mTimerRunning) {
                showDialog()
            }
        })
        startPause.setOnClickListener(View.OnClickListener {
            if (mTimeLeftInMillis == 0L) {
                Toast.makeText(this@MainActivity, "Please seat the time.", Toast.LENGTH_LONG).show()
            } else {
                if (mTimerRunning) {
                    pauseTimer()
                } else {
                    startTimer()
                }
            }
        })
        stop.setOnClickListener(View.OnClickListener { resetTimer() })
        fab_List.setOnClickListener(View.OnClickListener {
            if (mTimerRunning) {
                val recordTime: Long = (totalProgress - runingProgress).toLong()
                val message = sqliteDB.addData(submit, recordTime.toString())
                Toast.makeText(this, "" + message, Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, TimerListActivity::class.java)
                startActivity(intent)
            }
        })
        fab_List.visibility = View.VISIBLE
        fab_List.setImageResource(R.drawable.ic_list_numb)
        updateCountDownText()
    }

    override fun onResume() {
        super.onResume()
        if (state.contains("Stop")) {
            removeAlarm(this@MainActivity)
            notificationUtil.hideTimerNotification(this)
        }
        Log.e(TAG, "onResume")
    }

    @SuppressLint("RestrictedApi")
    override fun onPause() {
        super.onPause()
        if (mTimerRunning) {
            secondsRemaining = runingProgress.toLong()
            AppConstants.nowSeconds = nowSeconds
            val wakeUpTime = setAlarm(this)
            notificationUtil.showTimerRunning(this@MainActivity)
            state = "onPause"
            Log.i(TAG, "onPause")
        }
        fab_List.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }

    @SuppressLint("RestrictedApi")
    private fun startTimer() {
        mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mTimeLeftInMillis = millisUntilFinished
                updateCountDownText()
                editor.putString(RUNNING, runingProgress.toString())
                editor.commit()
            }

            override fun onFinish() {
                mTimerRunning = false
                startPause.setImageResource(R.drawable.ic_play)
                sqliteDB.addData(submit, totalProgress.toString())
                notificationUtil.showTimerExpired(this@MainActivity)
                fab_List.setImageResource(R.drawable.ic_list_numb)
            }
        }.start()
        mTimerRunning = true
        submit = dateFormat.format(Calendar.getInstance().time)
        startPause.setImageResource(R.drawable.ic_pause)
        fab_List.visibility = View.VISIBLE
        fab_List.setImageResource(R.drawable.ic_playlist_add)
    }


    @SuppressLint("RestrictedApi")
    private fun pauseTimer() {
        mCountDownTimer.cancel()
        mTimerRunning = false
        startPause.setImageResource(R.drawable.ic_play)
        fab_List.visibility = View.GONE
        editor.putString(RUNNING, runingProgress.toString())
        editor.commit()
    }

    @SuppressLint("RestrictedApi")
    private fun resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS
        mCountDownTimer.cancel()
        mTimerRunning = false
        updateCountDownText()
        startPause.setImageResource(R.drawable.ic_play)
        fab_List.visibility = View.VISIBLE
        fab_List.setImageResource(R.drawable.ic_list_numb)
    }

    private fun updateCountDownText() {
        val minutes = (mTimeLeftInMillis / 1000).toInt() / 60
        val seconds = (mTimeLeftInMillis / 1000).toInt() % 60
        runingProgress = (mTimeLeftInMillis / 1000).toInt()
        progress_countdown.progress = runingProgress
        val timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        runingTime = timeLeftFormatted
        mTextViewCountDown.text = timeLeftFormatted
    }

    private fun showDialog() {
        val view = View.inflate(this, R.layout.time_dialog, null)
        val cancel = view.findViewById<Button>(R.id.cancel)
        val ok = view.findViewById<Button>(R.id.ok)
        val builder = AlertDialog.Builder(this)
        builder.setView(view)
        val numberPickerMinutes = view.findViewById<NumberPicker>(R.id.numpicker_minutes)
        numberPickerMinutes.maxValue = 59
        val numberPickerSeconds = view.findViewById<NumberPicker>(R.id.numpicker_seconds)
        numberPickerSeconds.maxValue = 59
        val alertDialog = builder.create()
        cancel.setOnClickListener { alertDialog.dismiss() }
        ok.setOnClickListener {
            mTimeLeftInMillis =
                (numberPickerMinutes.value * 60000 + numberPickerSeconds.value * 1000).toLong()
            updateCountDownText()
            totalProgress = (mTimeLeftInMillis / 1000).toInt()
            setProgressBarValues()
            editor.putString(TOTAL, totalProgress.toString())
            editor.commit()
            editor.apply()
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    private fun setProgressBarValues() {
        progress_countdown.max = totalProgress
        progress_countdown.progress = runingProgress
    }

    fun callShare() {
        totalProgress = preferences.getString(TOTAL, "")?.toInt() ?: 0
        runingProgress = preferences.getString(RUNNING, "")?.toInt() ?: 0
        nowSeconds = preferences.getString(WALCK, "")?.toLong() ?: 0
        Toast.makeText(this, "$runingProgress $totalProgress", Toast.LENGTH_SHORT)
            .show()
    }

}