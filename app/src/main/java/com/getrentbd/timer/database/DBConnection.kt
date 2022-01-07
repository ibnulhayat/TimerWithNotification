package com.getrentbd.timer.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import com.getrentbd.timer.entity.TimerEntity
import com.getrentbd.timer.model.TimerList

class DBConnection(context: Context?) :
    SQLiteOpenHelper(context, DATABSE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val sql = "CREATE TABLE " + TimerEntity.TABLE_NAME + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY," +
                TimerEntity.START_TIME + " TEXT," +
                TimerEntity.STOP_TIME + " TEXT)"
        db.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(TimerEntity.TABLE_UPGRADE)
        onCreate(db)
    }

    fun addData(startTime: String?, stopTime: String?): String? {
        var message: String? = null
        val database: SQLiteDatabase = writableDatabase
        val registrationDataSave = ContentValues()
        registrationDataSave.put(TimerEntity.START_TIME, startTime)
        registrationDataSave.put(TimerEntity.STOP_TIME, stopTime)
        val resultOfRegistration: Long =
            database.insert(TimerEntity.TABLE_NAME, null, registrationDataSave)
        if (resultOfRegistration > 0) {
            println("Data save successfully")
            message = "Data Added"
        }
        return message
    }

    var sqLiteDatabase: SQLiteDatabase = this.readableDatabase
    fun checkTimerList(): ArrayList<TimerList> {
        val data: ArrayList<TimerList> = ArrayList<TimerList>()
        try {
            val sortOrder: String = BaseColumns._ID+ " DESC"

            val cursor: Cursor = sqLiteDatabase.query(
                TimerEntity.TABLE_NAME,  // The table to query
                null,  // The array of columns to return (pass null to get all)
                null,  // The columns for the WHERE clause
                null,  // The values for the WHERE clause
                null,  // don't group the rows
                null,  // don't filter by row groups
                sortOrder // The sort order
            )
            while (cursor.moveToNext()) {
                data.add(TimerList(cursor.getString(0), cursor.getString(1), cursor.getString(2)))
            }
        } catch (ex: Exception) {
            ex.message?.let { Log.e("DBERROR", it) }
        }
        return data
    }

    fun deleteData(id: Int): Boolean {
        val db: SQLiteDatabase = writableDatabase
        val selection: String = BaseColumns._ID + "=?"
        val selectionArgs = arrayOf(id.toString())
        val deleteResult: Int = db.delete(TimerEntity.TABLE_NAME, selection, selectionArgs)
        return if (deleteResult > 0) java.lang.Boolean.TRUE else java.lang.Boolean.FALSE
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABSE_NAME = "timerDatabase.db"
    }
}