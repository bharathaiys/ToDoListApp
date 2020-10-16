package com.teamnougat.todolistapp.db;

import android.provider.BaseColumns;

public class TaskContract {
    public static final String DB_NAME = "com.teamnougat.todolist";
    public static final int DB_VERSION = 1;

    public class TaskEntry implements BaseColumns {
        public static final String TABLE = "tasks";
        public static final String COL_TASK_ID = "id";
        public static final String COL_TASK_TITLE = "title";
        public static final String COL_TASK_TYPE = "type";
        public static final String COL_TASK_DUEDATE = "duedate";
        public static final String COL_TASK_DUEDAY = "dueday";
        public static final String COL_TASK_REMINDER = "reminder";
        public static final String COL_TASK_LOCATION = "location";
        public static final String COL_TASK_KEY = "key";
    }
}
