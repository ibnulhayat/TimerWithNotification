package com.getrentbd.timer.activity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.getrentbd.timer.R
import com.getrentbd.timer.database.DBConnection
import com.getrentbd.timer.model.TimerList

class TimerListAdapter(private var mContext: Context, lists: ArrayList<TimerList>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private val timerLists: ArrayList<TimerList> = lists
    private val sqliteDB: DBConnection = DBConnection(mContext)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mContext = parent.context
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val list: TimerList = timerLists[position]
        val sNo: String = list.id
        val sTime: String = list.startTime
        val stopTime: String = list.stopTime
        val time = sTime.split("/").toTypedArray()
        val vHolder = holder as ViewHolder
        vHolder.tvNo.text = sNo
        vHolder.tvStartTime.text = """
        ${time[0]}
        ${time[1]}
        """.trimIndent()
        vHolder.tvStopTime.text = stopTime
        vHolder.ivDelete.setOnClickListener {
            val tt: Boolean = sqliteDB.deleteData(sNo.toInt())
            Toast.makeText(mContext, "" + tt, Toast.LENGTH_SHORT).show()
            timerLists.removeAt(position)
            notifyDataSetChanged()
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNo: TextView = itemView.findViewById(R.id.tvNo)
        val tvStartTime: TextView = itemView.findViewById(R.id.tvStartTime)
        val tvStopTime: TextView = itemView.findViewById(R.id.tvStopTime)
        val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)

    }

    override fun getItemCount(): Int {
        return timerLists.size
    }

}