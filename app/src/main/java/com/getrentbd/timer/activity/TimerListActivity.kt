package com.getrentbd.timer.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.getrentbd.timer.R
import com.getrentbd.timer.database.DBConnection
import com.getrentbd.timer.model.TimerList

class TimerListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var sqliteDB: DBConnection
    private lateinit var lists: ArrayList<TimerList>
    private lateinit var adapter: TimerListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer_list)
    }

    override fun onStart() {
        super.onStart()
        lists = ArrayList()
        sqliteDB = DBConnection(this)
        lists = sqliteDB.checkTimerList()
        recyclerView = findViewById(R.id.recyclerView)
        val manager = LinearLayoutManager(this)
        recyclerView.layoutManager = manager
        adapter = TimerListAdapter(this, lists)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}