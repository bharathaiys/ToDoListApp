package com.teamnougat.todolistapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TaskDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "TaskDbHelper";

    public TaskDbHelper(Context context) {
        super(context, TaskContract.DB_NAME, null, TaskContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TaskContract.TaskEntry.TABLE + " ( " +
                TaskContract.TaskEntry.COL_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TaskContract.TaskEntry.COL_TASK_TITLE + " TEXT NOT NULL, " +
                TaskContract.TaskEntry.COL_TASK_TYPE + " CHAR(15) NOT NULL, " +
                TaskContract.TaskEntry.COL_TASK_DUEDATE + " DATETIME NOT NULL, " +
                TaskContract.TaskEntry.COL_TASK_DUEDAY + " CHAR(15) NOT NULL, " +
                TaskContract.TaskEntry.COL_TASK_REMINDER + " TIME, " +
                TaskContract.TaskEntry.COL_TASK_LOCATION + " TEXT, " +
                TaskContract.TaskEntry.COL_TASK_KEY + " INTEGER(1) NOT NULL DEFAULT 1); ";
        db.execSQL(createTable);
        Log.d(TAG, createTable);
        /*
            CREATE TABLE IF NOT EXISTS(
                ID INTEGER PRIMARY KEY AUTOINCREMENT,
                TITLE TEXT NOT NULL,
                TYPE CHAR(15) NOT NULL,
                DUEDATE DATE NOT NULL,
                DUETIME TIME,
                REMINDER TIME,
                LOCATION TEXT,
                KEY INTEGER(1) NOT NULL DEFAULT 1);
         */
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE);
        onCreate(db);
    }
}
